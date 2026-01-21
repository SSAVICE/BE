package teamssavice.ssavice.book.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
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
public class Book extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_item_id", nullable = false)
    private ServiceItem serviceItem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookStatus bookStatus;

    @NotNull
    @Column(nullable = false)
    @Builder.Default
    private boolean isReviewed = false;

    public void cancel() {
        if (this.bookStatus == BookStatus.CANCELED) {
            throw new ConflictException(ErrorCode.ALREADY_CANCELED);
        }
        this.bookStatus = BookStatus.CANCELED;
    }
}
