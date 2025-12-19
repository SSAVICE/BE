package teamssavice.ssavice.company.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import teamssavice.ssavice.global.entity.BaseEntity;
import teamssavice.ssavice.address.Address;
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

    @Column(nullable = true)
    private String imageUrl;
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
    private boolean isDeleted = false;

    @Column(nullable = true)
    private String detail;

    // boolean isApproved; <-- 업체 등록 시 승인 여부

    //주소
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;
    //유저
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;
}
