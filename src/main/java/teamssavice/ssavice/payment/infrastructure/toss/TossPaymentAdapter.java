package teamssavice.ssavice.payment.infrastructure.toss;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import teamssavice.ssavice.global.constants.ErrorCode;
import teamssavice.ssavice.global.exception.ExternalApiException;
import teamssavice.ssavice.payment.infrastructure.toss.client.TossPaymentClient;
import teamssavice.ssavice.payment.infrastructure.toss.dto.TossCancelRequest;
import teamssavice.ssavice.payment.infrastructure.toss.dto.TossCancelResponse;
import teamssavice.ssavice.payment.infrastructure.toss.dto.TossConfirmRequest;
import teamssavice.ssavice.payment.infrastructure.toss.dto.TossConfirmResponse;
import teamssavice.ssavice.payment.service.client.PaymentClient;
import teamssavice.ssavice.payment.service.client.dto.PaymentCancelCommand;
import teamssavice.ssavice.payment.service.client.dto.PaymentCancelResult;
import teamssavice.ssavice.payment.service.client.dto.PaymentConfirmCommand;
import teamssavice.ssavice.payment.service.client.dto.PaymentConfirmResult;

@Slf4j
@Component
@RequiredArgsConstructor
public class TossPaymentAdapter implements PaymentClient {

    private final TossPaymentClient tossPaymentClient;

    @Override
    public PaymentConfirmResult confirm(PaymentConfirmCommand command) {
        TossConfirmRequest request = new TossConfirmRequest(
            command.paymentKey(),
            command.orderId(),
            command.amount()
        );

        try {
            TossConfirmResponse response = tossPaymentClient.confirm(request);
            return new PaymentConfirmResult(
                response.paymentKey(),
                response.orderId(),
                response.status(),
                response.totalAmount(),
                response.method(),
                response.approvedAt()
            );
        } catch (feign.RetryableException e) {
            throw new ExternalApiException(ErrorCode.EXTERNAL_API_TIMEOUT);
        } catch (FeignException e) {
            log.error("Toss payment confirm failed. status={}, body={}", e.status(), e.contentUTF8());
            throw new ExternalApiException(ErrorCode.TOSS_PAYMENT_CONFIRM_FAILED);
        }
    }

    @Override
    public PaymentCancelResult cancel(PaymentCancelCommand command) {
        TossCancelRequest request = new TossCancelRequest(command.cancelReason());

        try {
            TossCancelResponse response = tossPaymentClient.cancel(command.paymentKey(), request);
            return new PaymentCancelResult(
                response.paymentKey(),
                response.orderId(),
                response.status()
            );
        } catch (feign.RetryableException e) {
            throw new ExternalApiException(ErrorCode.EXTERNAL_API_TIMEOUT);
        } catch (FeignException e) {
            log.error("Toss payment cancel failed. paymentKey={}, status={}, body={}",
                command.paymentKey(), e.status(), e.contentUTF8());
            throw new ExternalApiException(ErrorCode.TOSS_PAYMENT_CANCEL_FAILED);
        }
    }
}
