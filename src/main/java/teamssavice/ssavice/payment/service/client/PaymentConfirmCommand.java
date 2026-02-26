package teamssavice.ssavice.payment.service.client;

public record PaymentConfirmCommand(
        String paymentKey,
        String orderId,
        Long amount
) {
}
