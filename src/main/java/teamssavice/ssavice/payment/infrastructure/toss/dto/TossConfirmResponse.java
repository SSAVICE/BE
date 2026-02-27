package teamssavice.ssavice.payment.infrastructure.toss.dto;

public record TossConfirmResponse(
        String paymentKey,
        String orderId,
        String status,
        Long totalAmount,
        String method,
        String approvedAt
) {
}
