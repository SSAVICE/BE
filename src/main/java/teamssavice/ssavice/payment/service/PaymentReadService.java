package teamssavice.ssavice.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.global.constants.ErrorCode;
import teamssavice.ssavice.global.exception.EntityNotFoundException;
import teamssavice.ssavice.payment.entity.Payment;
import teamssavice.ssavice.payment.entity.PaymentStatus;
import teamssavice.ssavice.payment.infrastructure.repository.PaymentRepository;

@Service
@RequiredArgsConstructor
public class PaymentReadService {

    private final PaymentRepository paymentRepository;

    @Transactional(readOnly = true)
    public Payment findByOrderIdWithUserAndServiceItem(String orderId) {
        return paymentRepository.findByOrderIdWithUserAndServiceItem(orderId)
            .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PAYMENT_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public boolean existsPendingPayment(Long userId, Long serviceItemId) {
        return paymentRepository.existsByUserIdAndServiceItemIdAndStatus(userId, serviceItemId, PaymentStatus.PENDING);
    }
}
