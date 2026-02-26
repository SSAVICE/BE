package teamssavice.ssavice.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.book.entity.BookStatus;
import teamssavice.ssavice.book.service.BookReadService;
import teamssavice.ssavice.global.constants.ErrorCode;
import teamssavice.ssavice.global.exception.ConflictException;
import teamssavice.ssavice.global.exception.ExternalApiException;
import teamssavice.ssavice.global.exception.ForbiddenException;
import teamssavice.ssavice.payment.entity.Payment;
import teamssavice.ssavice.payment.service.client.PaymentClient;
import teamssavice.ssavice.payment.service.client.dto.PaymentCancelCommand;
import teamssavice.ssavice.payment.service.client.dto.PaymentConfirmCommand;
import teamssavice.ssavice.payment.service.client.dto.PaymentConfirmResult;
import teamssavice.ssavice.payment.service.dto.PaymentCommand;
import teamssavice.ssavice.payment.service.dto.PaymentModel;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentReadService paymentReadService;
    private final PaymentWriteService paymentWriteService;
    private final PaymentClient paymentClient;
    private final BookReadService bookReadService;

    /**
     * 결제 준비 - PENDING Payment 생성.
     * 중복 예약(취소 제외) 또는 중복 PENDING Payment가 있으면 ConflictException 발생.
     */
    @Transactional
    public PaymentModel.Prepare createPendingPayment(PaymentCommand.Prepare command) {
        if (bookReadService.existsByUserAndServiceAndStatusNot(command.userId(), command.serviceItemId(), BookStatus.CANCELED)) {
            throw new ConflictException(ErrorCode.ALREADY_APPLIED);
        }

        if (paymentReadService.existsPendingPayment(command.userId(), command.serviceItemId())) {
            throw new ConflictException(ErrorCode.DUPLICATE_PENDING_PAYMENT);
        }

        Payment payment = paymentWriteService.createPendingPayment(command.userId(), command.serviceItemId());
        String customerKey = UUID.nameUUIDFromBytes(("user:" + command.userId()).getBytes()).toString();
        return PaymentModel.Prepare.from(customerKey, payment);
    }

    public void confirmPayment(PaymentCommand.Confirm command) {
        Payment payment = paymentReadService.findByOrderIdWithUserAndServiceItem(command.orderId());
        validatePaymentOwner(payment, command.userId());

        PaymentConfirmResult result = paymentClient.confirm(
            new PaymentConfirmCommand(command.paymentKey(), command.orderId(), payment.getAmount()));

        if (!result.isApproved()) {
            log.error("Toss 결제 승인 상태가 DONE이 아닙니다. orderId={}, status={}", command.orderId(), result.status());
            throw new ExternalApiException(ErrorCode.PAYMENT_CONFIRM_STATUS_INVALID);
        }

        try {
            paymentWriteService.completePayment(command.orderId(), result.paymentKey(), result.method());
        } catch (RuntimeException e) {
            cancelPaymentAfterFailure(command.orderId(), result.paymentKey(), e);
        }
    }

    /**
     * Toss 결제 실패 콜백 처리 - PENDING Payment를 FAILED 상태로 변경.
     */
    @Transactional
    public void failPayment(PaymentCommand.Fail command) {
        Payment payment = paymentReadService.findByOrderIdWithUserAndServiceItem(command.orderId());
        validatePaymentOwner(payment, command.userId());
        paymentWriteService.failPayment(command.orderId());
    }

    private void validatePaymentOwner(Payment payment, Long userId) {
        if (!payment.getUser().getId().equals(userId)) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN);
        }
    }

    private void cancelPaymentAfterFailure(String orderId, String paymentKey, RuntimeException cause) {
        try {
            paymentClient.cancel(new PaymentCancelCommand(paymentKey, "정원 초과 결제 취소"));
            paymentWriteService.failPayment(orderId);
            throw cause;
        } catch (RuntimeException cancelException) {
            if (cancelException == cause) {
                throw cause;
            }
            throw new ExternalApiException(ErrorCode.TOSS_PAYMENT_CANCEL_FAILED);
        }
    }
}
