package teamssavice.ssavice.payment.service.client.dto;

public record PaymentCancelResult(
    String paymentKey,
    String orderId,
    String status
) {
}
