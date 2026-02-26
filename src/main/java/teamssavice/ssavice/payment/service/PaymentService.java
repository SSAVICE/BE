package teamssavice.ssavice.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import teamssavice.ssavice.global.constants.ErrorCode;
import teamssavice.ssavice.global.exception.ConflictException;
import teamssavice.ssavice.global.exception.ExternalApiException;
import teamssavice.ssavice.payment.entity.Payment;
import teamssavice.ssavice.payment.service.client.PaymentClient;
import teamssavice.ssavice.payment.service.client.dto.PaymentCancelCommand;
import teamssavice.ssavice.payment.service.client.dto.PaymentCancelResult;
import teamssavice.ssavice.payment.service.client.dto.PaymentConfirmCommand;
import teamssavice.ssavice.payment.service.client.dto.PaymentConfirmResult;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;
import teamssavice.ssavice.serviceItem.service.ServiceItemReadService;
import teamssavice.ssavice.user.entity.Users;
import teamssavice.ssavice.user.service.UserReadService;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentReadService paymentReadService;
    private final PaymentWriteService paymentWriteService;
    private final PaymentClient paymentClient;
    private final UserReadService userReadService;
    private final ServiceItemReadService serviceItemReadService;

    /**
     * 결제 준비 - PENDING Payment 생성.
     * 동일 사용자/서비스 조합의 중복 PENDING Payment가 있으면 ConflictException 발생.
     */
    public Payment createPendingPayment(Long userId, Long serviceItemId, Long amount) {
        if (paymentReadService.existsPendingPayment(userId, serviceItemId)) {
            throw new ConflictException(ErrorCode.DUPLICATE_PENDING_PAYMENT);
        }

        Users user = userReadService.findById(userId);
        ServiceItem serviceItem = serviceItemReadService.findById(serviceItemId);

        return paymentWriteService.createPendingPayment(user, serviceItem, amount);
    }

    public void confirmPayment(String paymentKey, String orderId, Long amount) {
        Payment payment = paymentReadService.findByOrderId(orderId);

        if (!payment.getAmount().equals(amount)) {
            throw new ConflictException(ErrorCode.PAYMENT_AMOUNT_MISMATCH);
        }

        PaymentConfirmResult result = paymentClient.confirm(
            new PaymentConfirmCommand(paymentKey, orderId, amount));

        if (!result.isApproved()) {
            log.error("Toss 결제 승인 상태가 DONE이 아닙니다. orderId={}, status={}", orderId, result.status());
            throw new ExternalApiException(ErrorCode.PAYMENT_CONFIRM_STATUS_INVALID);
        }

        try {
            paymentWriteService.completePayment(orderId, result.paymentKey(), result.method());
        } catch (RuntimeException e) {
            cancelPaymentAfterFailure(orderId, result.paymentKey(), e);
        }
    }

    private void cancelPaymentAfterFailure(String orderId, String paymentKey, RuntimeException cause) {
        log.error("결제 완료 처리 실패. Toss 결제 취소를 시도합니다. orderId={}, paymentKey={}", orderId, paymentKey);
        try {
            PaymentCancelResult cancelResult = paymentClient.cancel(
                new PaymentCancelCommand(paymentKey, "정원 초과 결제 취소"));
            log.info("Toss 결제 취소 성공. paymentKey={}, cancelStatus={}", paymentKey, cancelResult.status());
            paymentWriteService.failPayment(orderId);
            throw cause;
        } catch (ExternalApiException cancelException) {
            log.error("Toss 결제 취소 실패. 수동 처리가 필요합니다. paymentKey={}, error={}",
                paymentKey, cancelException.getMessage(), cancelException);
            throw new ExternalApiException(ErrorCode.TOSS_PAYMENT_CANCEL_FAILED);
        }
    }
}
