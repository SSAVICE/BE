package teamssavice.ssavice.fixture;

import org.springframework.test.util.ReflectionTestUtils;
import teamssavice.ssavice.payment.entity.Payment;
import teamssavice.ssavice.payment.entity.PaymentStatus;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;
import teamssavice.ssavice.user.entity.Users;

import java.util.UUID;

public class PaymentFixture {

    public static Payment pending(Users user, ServiceItem serviceItem) {
        Payment payment = Payment.builder()
                .user(user)
                .serviceItem(serviceItem)
                .orderId(UUID.randomUUID().toString())
                .amount(serviceItem.getPrice().getDiscountedPrice())
                .build();
        ReflectionTestUtils.setField(payment, "id", 1L);
        return payment;
    }

    public static Payment approved(Users user, ServiceItem serviceItem) {
        Payment payment = Payment.builder()
                .user(user)
                .serviceItem(serviceItem)
                .orderId(UUID.randomUUID().toString())
                .paymentKey("toss-payment-key-" + UUID.randomUUID())
                .amount(serviceItem.getPrice().getDiscountedPrice())
                .method("카드")
                .status(PaymentStatus.APPROVED)
                .build();
        ReflectionTestUtils.setField(payment, "id", 2L);
        return payment;
    }
}
