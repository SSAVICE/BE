package teamssavice.ssavice.payment.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import teamssavice.ssavice.payment.service.dto.PaymentCommand;

public class PaymentRequest {

    public record Prepare(
        @NotNull
        Long serviceItemId
    ) {
        public PaymentCommand.Prepare toCommand(Long userId) {
            return PaymentCommand.Prepare.builder()
                .userId(userId)
                .serviceItemId(serviceItemId)
                .build();
        }
    }

    public record Confirm(
        @NotBlank
        String paymentKey,

        @NotBlank
        String orderId
    ) {
        public PaymentCommand.Confirm toCommand(Long userId) {
            return PaymentCommand.Confirm.builder()
                .userId(userId)
                .paymentKey(paymentKey)
                .orderId(orderId)
                .build();
        }
    }

    public record Fail(
        @NotBlank
        String orderId,

        @NotBlank
        String code,

        @NotBlank
        String message
    ) {
        public PaymentCommand.Fail toCommand(Long userId) {
            return PaymentCommand.Fail.builder()
                .userId(userId)
                .orderId(orderId)
                .errorCode(code)
                .errorMessage(message)
                .build();
        }
    }
}
