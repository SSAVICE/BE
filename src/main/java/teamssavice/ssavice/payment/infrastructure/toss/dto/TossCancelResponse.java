package teamssavice.ssavice.payment.infrastructure.toss.dto;

public record TossCancelResponse(
        String paymentKey,
        String orderId,
        String status
) {
}
