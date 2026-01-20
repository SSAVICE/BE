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
public class Wish extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceItem serviceItem;
}
