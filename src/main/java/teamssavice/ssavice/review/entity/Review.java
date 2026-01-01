package teamssavice.ssavice.review.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import teamssavice.ssavice.global.entity.BaseEntity;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder
@Getter
@AllArgsConstructor
public class Review extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private Long companyId;

    @NotNull
    @Column(nullable = false)
    private Integer rating;

    @NotNull
    @Column(nullable = false)
    private String comment;

    @Embedded
    private UserName userName;

    @NotNull
    @Column(nullable = false)
    private String serviceName;

    @NotNull
    @Column(nullable = false)
    private Long userId;

    @NotNull
    @Column(nullable = false)
    private Long serviceId;
}
