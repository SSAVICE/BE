package teamssavice.ssavice.payment.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import teamssavice.ssavice.payment.entity.Payment;
import teamssavice.ssavice.payment.entity.PaymentStatus;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrderId(String orderId);

    @Query("SELECT p FROM Payment p " +
        "JOIN FETCH p.user " +
        "JOIN FETCH p.serviceItem " +
        "WHERE p.orderId = :orderId")
    Optional<Payment> findByOrderIdWithUserAndServiceItem(@Param("orderId") String orderId);

    boolean existsByUserIdAndServiceItemIdAndStatus(Long userId, Long serviceItemId, PaymentStatus status);
}
