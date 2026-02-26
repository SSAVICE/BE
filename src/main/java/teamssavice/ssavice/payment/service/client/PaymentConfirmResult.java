package teamssavice.ssavice.payment.service.client;

public record PaymentConfirmResult(
        String paymentKey,
        String orderId,
        String status,
        Long totalAmount,
        String method,
        String approvedAt
) {
}
