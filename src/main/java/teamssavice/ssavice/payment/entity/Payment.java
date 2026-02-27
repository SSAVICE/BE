package teamssavice.ssavice.payment.entity;

import jakarta.persistence.*;
import lombok.*;
import teamssavice.ssavice.book.entity.Book;
import teamssavice.ssavice.global.constants.ErrorCode;
import teamssavice.ssavice.global.entity.BaseEntity;
import teamssavice.ssavice.global.exception.ConflictException;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;
import teamssavice.ssavice.user.entity.Users;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@AllArgsConstructor
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_item_id", nullable = false)
    private ServiceItem serviceItem;

    /**
     * 결제 승인 후 Book 생성 시점에 linkBook()으로 연결된다.
     * Payment 생성 시점에는 null이므로 nullable = true.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = true)
    private Book book;

    @Column(nullable = false, unique = true)
    private String orderId;

    @Column(unique = true)
    private String paymentKey;

    @Column(nullable = false)
    private Long amount;

    private String method; //결제 수단

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;

    public void confirm(String paymentKey, String method) {
        if (this.status != PaymentStatus.PENDING) {
            throw new ConflictException(ErrorCode.INVALID_PAYMENT_STATE);
        }
        this.paymentKey = paymentKey;
        this.method = method;
        this.status = PaymentStatus.APPROVED;
    }

    public void fail() {
        if (this.status != PaymentStatus.PENDING) {
            throw new ConflictException(ErrorCode.INVALID_PAYMENT_STATE);
        }
        this.status = PaymentStatus.FAILED;
    }

    public void refund() {
        if (this.status != PaymentStatus.APPROVED) {
            throw new ConflictException(ErrorCode.INVALID_PAYMENT_STATE);
        }
        this.status = PaymentStatus.REFUNDED;
    }

    public void linkBook(Book book) {
        if (this.book != null) {
            throw new ConflictException(ErrorCode.INVALID_PAYMENT_STATE);
        }
        this.book = book;
    }
}
