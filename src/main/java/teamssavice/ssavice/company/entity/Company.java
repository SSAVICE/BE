package teamssavice.ssavice.company.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import teamssavice.ssavice.address.AddressCommand;
import teamssavice.ssavice.company.service.dto.CompanyCommand;
import teamssavice.ssavice.global.entity.BaseEntity;
import teamssavice.ssavice.address.Address;
import teamssavice.ssavice.imageresource.entity.ImageResource;
import teamssavice.ssavice.user.entity.Users;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder
@Getter
@AllArgsConstructor
public class Company extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private String companyName;
    @NotNull
    @Column(nullable = false)
    private String ownerName;
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

    // boolean isApproved; <-- 업체 등록 시 승인 여부

    //주소
    @OneToOne(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;
    //유저
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;


    public void update(CompanyCommand.Update command) {
        if (command.companyName() != null) {
            this.companyName = command.companyName();
        }
        if(command.ownerName() != null) {
            this.ownerName = command.ownerName();
        }
        if(command.phoneNumber() != null) {
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
        address.update(AddressCommand.Update.from(command));
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
}
