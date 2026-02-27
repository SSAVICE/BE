package teamssavice.ssavice.payment.service.client.dto;

public record PaymentConfirmResult(
    String paymentKey,
    String orderId,
    String status,
    Long totalAmount,
    String method,
    String approvedAt
) {

    public boolean isApproved() {
        return TossPaymentStatus.DONE.name().equals(status);
    }

    public enum TossPaymentStatus {
        DONE, CANCELED, EXPIRED, PARTIAL_CANCELED, ABORTED, WAITING_FOR_DEPOSIT, IN_PROGRESS, READY
    }
}
