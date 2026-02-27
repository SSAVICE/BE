package teamssavice.ssavice.payment.service.dto;

import lombok.Builder;
import teamssavice.ssavice.payment.entity.Payment;

public class PaymentModel {

    @Builder
    public record Prepare(
        String customerKey,
        String orderId,
        String orderName,
        Long amount
    ) {
        public static Prepare from(String customerKey, Payment payment) {
            return Prepare.builder()
                .customerKey(customerKey)
                .orderId(payment.getOrderId())
                .orderName(payment.getServiceItem().getTitle())
                .amount(payment.getAmount())
                .build();
        }
    }
}
