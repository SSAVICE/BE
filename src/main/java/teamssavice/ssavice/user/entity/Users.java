package teamssavice.ssavice.user.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamssavice.ssavice.address.Address;
import teamssavice.ssavice.global.entity.BaseEntity;
import teamssavice.ssavice.imageresource.constants.ImageConstants;
import teamssavice.ssavice.imageresource.entity.ImageResource;
import teamssavice.ssavice.user.constants.Provider;
import teamssavice.ssavice.user.constants.UserRole;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"provider_id", "provider"}))
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
    @Enumerated(EnumType.STRING)
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

    @NotNull
    @Column(nullable = false)
    private String providerId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_resource_id")
    private ImageResource imageResource;

    @NotNull
    @Column(nullable = false)
    @Builder.Default
    private boolean isDeleted = false;

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
