package teamssavice.ssavice.payment.service.client;

import teamssavice.ssavice.payment.service.client.dto.PaymentCancelCommand;
import teamssavice.ssavice.payment.service.client.dto.PaymentCancelResult;
import teamssavice.ssavice.payment.service.client.dto.PaymentConfirmCommand;
import teamssavice.ssavice.payment.service.client.dto.PaymentConfirmResult;

public interface PaymentClient {
    PaymentConfirmResult confirm(PaymentConfirmCommand command);

    PaymentCancelResult cancel(PaymentCancelCommand command);
}
