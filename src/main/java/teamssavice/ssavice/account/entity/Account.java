package teamssavice.ssavice.account.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import teamssavice.ssavice.account.constants.Provider;
import teamssavice.ssavice.auth.constants.Role;
import teamssavice.ssavice.global.entity.BaseEntity;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"provider", "provider_id", "role"}))
@Builder
@Getter
@AllArgsConstructor
public class Account extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private Provider provider;

    @NotNull
    @Column(name = "provider_id", nullable = false)
    private String providerId;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private Role role;

    @NotNull
    @Column(nullable = false)
    @Builder.Default
    private boolean isDeleted = false;


    public void deleteAccount() {
        this.isDeleted = true;
    }

    public void restore() {
        this.isDeleted = false;
    }

}
