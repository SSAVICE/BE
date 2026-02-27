package teamssavice.ssavice.payment.service.client.dto;

public record PaymentConfirmCommand(
    String paymentKey,
    String orderId,
    Long amount
) {
}
