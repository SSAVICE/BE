package teamssavice.ssavice.payment.service.client;

public interface PaymentClient {
    PaymentConfirmResult confirm(PaymentConfirmCommand command);
}
