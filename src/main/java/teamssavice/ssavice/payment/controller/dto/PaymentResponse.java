package teamssavice.ssavice.payment.controller.dto;

import lombok.Builder;
import teamssavice.ssavice.payment.service.dto.PaymentModel;

public class PaymentResponse {

    @Builder
    public record Prepare(
        String orderId,
        String orderName,
        Long amount
    ) {
        public static Prepare from(PaymentModel.Prepare model) {
            return Prepare.builder()
                .orderId(model.orderId())
                .orderName(model.orderName())
                .amount(model.amount())
                .build();
        }
    }
}
