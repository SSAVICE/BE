package teamssavice.ssavice.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import teamssavice.ssavice.account.entity.Account;
import teamssavice.ssavice.address.Address;
import teamssavice.ssavice.global.entity.BaseEntity;
import teamssavice.ssavice.imageresource.constants.ImageConstants;
import teamssavice.ssavice.imageresource.entity.ImageResource;
import teamssavice.ssavice.user.constants.UserRole;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder
@Getter
@AllArgsConstructor
public class Users extends BaseEntity {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private Account account;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private UserRole userRole;

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


    @OneToOne(
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    @JoinColumn(name = "address_id")
    private Address address;

    public void updateImage(ImageResource imageResource) {
        if (this.imageResource != null) {
            this.imageResource.deActivate();
        }
        this.imageResource = imageResource;
        imageResource.activate();
    }

    public boolean hasImageResource() {
        return this.getImageResource() != null;
    }

    public void modify(String name, String email, String phoneNumber) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public void updateAddress(Address address) {
        this.address = address;
    }

    public String getObjectKey() {
        String objectKey = ImageConstants.DEFAULT_PROFILE_IMAGE_OBJECT_KEY;
        if (this.hasImageResource()) {
            objectKey = this.imageResource.getResolveKey();
        }
        return objectKey;
    }
}
