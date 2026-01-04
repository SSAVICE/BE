package teamssavice.ssavice.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import teamssavice.ssavice.address.Address;
import teamssavice.ssavice.global.entity.BaseEntity;
import teamssavice.ssavice.imageresource.entity.ImageResource;
import teamssavice.ssavice.user.constants.Provider;
import teamssavice.ssavice.user.constants.UserRole;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder
@Getter
@AllArgsConstructor
public class Users extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private UserRole userRole;
    @NotNull
    @Column(nullable = false)
    private Provider provider;
    @NotNull
    @Column(nullable = false)
    private String name;
    @NotNull
    @Column(nullable = false, unique = true)
    private String email;
    @NotNull
    @Column(nullable = false)
    private String phoneNumber;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_resource_id")
    private ImageResource imageResource;

    @NotNull
    @Column(nullable = false)
    @Builder.Default
    private boolean isDeleted = false;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address address;

    public void updateImage(ImageResource imageResource) {
        if (this.imageResource != null) {
            this.imageResource.deActivate();
        }
        this.imageResource = imageResource;
    }
}
