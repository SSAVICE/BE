package teamssavice.ssavice.wish.entity;

import jakarta.persistence.*;
import lombok.*;
import teamssavice.ssavice.global.entity.BaseEntity;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;
import teamssavice.ssavice.user.entity.Users;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_wish_user_service",
                        columnNames = {"user_id", "service_item_id"}
                )
        }
)
public class Wish extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_wish_user"))
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_item_id", nullable = false, foreignKey = @ForeignKey(name = "fk_wish_service_item"))
    private ServiceItem serviceItem;

    public static Wish create(Users user, ServiceItem serviceItem) {
        return Wish.builder()
                .user(user)
                .serviceItem(serviceItem)
                .build();
    }

}
