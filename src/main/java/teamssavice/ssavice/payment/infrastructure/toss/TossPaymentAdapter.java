package teamssavice.ssavice.payment.infrastructure.toss;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import teamssavice.ssavice.global.constants.ErrorCode;
import teamssavice.ssavice.global.exception.ExternalApiException;
import teamssavice.ssavice.payment.infrastructure.toss.client.TossPaymentClient;
import teamssavice.ssavice.payment.infrastructure.toss.dto.TossConfirmRequest;
import teamssavice.ssavice.payment.infrastructure.toss.dto.TossConfirmResponse;
import teamssavice.ssavice.payment.service.client.PaymentClient;
import teamssavice.ssavice.payment.service.client.PaymentConfirmCommand;
import teamssavice.ssavice.payment.service.client.PaymentConfirmResult;

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
}
