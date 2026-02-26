package teamssavice.ssavice.payment.service.dto;

import lombok.Builder;
import teamssavice.ssavice.payment.entity.Payment;

public class PaymentModel {

    @Builder
    public record Prepare(
        String orderId,
        String orderName,
        Long amount
    ) {
        public static Prepare from(Payment payment) {
            return Prepare.builder()
                .orderId(payment.getOrderId())
                .orderName(payment.getServiceItem().getTitle())
                .amount(payment.getAmount())
                .build();
        }
    }
}
