package teamssavice.ssavice.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.book.entity.Book;
import teamssavice.ssavice.book.entity.BookStatus;
import teamssavice.ssavice.book.infrastructure.repository.BookRepository;
import teamssavice.ssavice.global.constants.ErrorCode;
import teamssavice.ssavice.global.exception.EntityNotFoundException;
import teamssavice.ssavice.payment.entity.Payment;
import teamssavice.ssavice.payment.infrastructure.repository.PaymentRepository;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;
import teamssavice.ssavice.serviceItem.infrastructure.repository.ServiceItemRepository;
import teamssavice.ssavice.user.entity.Users;
import teamssavice.ssavice.user.infrastructure.repository.UserRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentWriteService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final ServiceItemRepository serviceItemRepository;
    private final BookRepository bookRepository;

    @Transactional
    public Payment createPendingPayment(Long userId, Long serviceItemId) {
        Users user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
        ServiceItem serviceItem = serviceItemRepository.findById(serviceItemId)
            .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SERVICE_ITEM_NOT_FOUND));

        String orderId = UUID.randomUUID().toString();
        Payment payment = Payment.builder()
            .user(user)
            .serviceItem(serviceItem)
            .orderId(orderId)
            .amount(serviceItem.getPrice().getDiscountedPrice())
            .build();
        return paymentRepository.save(payment);
    }

    @Transactional
    public void completePayment(String orderId, String paymentKey, String method) {
        Payment payment = paymentRepository.findByOrderIdWithUserAndServiceItem(orderId)
            .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PAYMENT_NOT_FOUND));

        payment.confirm(paymentKey, method);

        ServiceItem serviceItem = serviceItemRepository.findByIdForUpdate(payment.getServiceItem().getId())
            .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SERVICE_ITEM_NOT_FOUND));

        serviceItem.validateAppliable();
        serviceItem.participate();

        Book book = Book.builder()
            .user(payment.getUser())
            .serviceItem(serviceItem)
            .bookStatus(BookStatus.RESERVED)
            .build();
        bookRepository.save(book);

        payment.linkBook(book);
    }

    @Transactional
    public void failPayment(String orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
            .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PAYMENT_NOT_FOUND));
        payment.fail();
    }
}
