package teamssavice.ssavice.company.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamssavice.ssavice.account.entity.Account;
import teamssavice.ssavice.address.Address;
import teamssavice.ssavice.company.service.dto.CompanyCommand;
import teamssavice.ssavice.global.entity.BaseEntity;
import teamssavice.ssavice.imageresource.constants.ImageConstants;
import teamssavice.ssavice.imageresource.entity.ImageResource;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder
@Getter
@AllArgsConstructor
public class Company extends BaseEntity {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private Account account;

    @NotNull
    @Column(nullable = false)
    private String companyName;
    @NotNull
    @Column(nullable = false)
    private String ownerName;
    @NotNull
    @Column(nullable = false)
    private String businessName; // 상호명
    @NotNull
    @Column(nullable = false)
    private String startDate;
    @NotNull
    @Column(nullable = false)
    private String phoneNumber;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_resource_id")
    private ImageResource imageResource;
    @NotNull
    @Column(nullable = false)
    private String businessNumber;

    @Column(nullable = true)
    private String description;
    @NotNull
    @Column(nullable = false)
    private String depositor;
    @NotNull
    @Column(nullable = false)
    private String accountNumber;
    @NotNull
    @Column(nullable = false)
    @Builder.Default
    private boolean isDeleted = false;

    @Column(nullable = true)
    private String detail;

    @Builder.Default
    @Column(nullable = false)
    private Long ratingSum = 0L;

    @Builder.Default
    @Column(nullable = false)
    private Long rateCount = 0L;


    // boolean isApproved; <-- 업체 등록 시 승인 여부

    //주소
    @OneToOne(
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;


    public void update(CompanyCommand.Update command) {
        if (command.companyName() != null) {
            this.companyName = command.companyName();
        }
        if (command.ownerName() != null) {
            this.ownerName = command.ownerName();
        }
        if (command.phoneNumber() != null) {
            this.phoneNumber = command.phoneNumber();
        }
        this.description = command.description();
        if (command.depositor() != null) {
            this.depositor = command.depositor();
        }
        if (command.accountNumber() != null) {
            this.accountNumber = command.accountNumber();
        }
        this.detail = command.detail();
    }

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

    public Float getAverageRate() {
        if (this.rateCount == 0) return 0.0F;
        return (float) this.ratingSum / this.rateCount;
    }

    public String getObjectKey() {
        String objectKey = ImageConstants.DEFAULT_COMPANY_IMAGE_OBJECT_KEY;
        if (this.hasImageResource()) {
            objectKey = this.getImageResource().getResolveKey();
        }
        return objectKey;
    }
}
