package teamssavice.ssavice.payment.service.dto;

import lombok.Builder;

public class PaymentCommand {

    @Builder
    public record Prepare(
        Long userId,
        Long serviceItemId
    ) {
    }

    @Builder
    public record Confirm(
        Long userId,
        String paymentKey,
        String orderId
    ) {
    }

    @Builder
    public record Fail(
        Long userId,
        String orderId,
        String errorCode,
        String errorMessage
    ) {
    }
}
