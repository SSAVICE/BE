package teamssavice.ssavice.payment.service.client.dto;

public record PaymentCancelCommand(
    String paymentKey,
    String cancelReason
) {
}
