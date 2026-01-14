package teamssavice.ssavice.serviceItem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;
import teamssavice.ssavice.address.Address;
import teamssavice.ssavice.company.entity.Company;
import teamssavice.ssavice.global.constants.ErrorCode;
import teamssavice.ssavice.global.entity.BaseEntity;
import teamssavice.ssavice.global.exception.ConflictException;
import teamssavice.ssavice.serviceItem.constants.ServiceStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@AllArgsConstructor
@SQLRestriction("is_deleted = false") //  만약 Company 측에서 삭제한 데이터에 대해서 봐야 하는 비즈니스 로직이 필요하다면 이 방법으로는 안하고 개선
public class ServiceItem extends BaseEntity {

    private static final String DEFAULT_IMAGE_URL = "https://placehold.co/400x400?text=SSAVICE";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private String title;

    @NotNull
    @Column(columnDefinition = "TEXT")
    private String description;

    @Embedded
    private Price price;

    @NotNull
    @Column(nullable = false)
    private Long minimumMember;

    @NotNull
    @Column(nullable = false)
    private Long maximumMember;

    @Builder.Default
    @Column(nullable = false)
    private Long currentMember = 0L;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime startDate;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime endDate;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime deadline; // 이벤트 마감 기간

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private ServiceStatus status = ServiceStatus.RECRUITING;

    private String category;

    private String tag; // 엘라스틱 서치 도입 예정

    @Builder.Default
    @Column(nullable = false)
    private boolean isDeleted = false;

    @Builder.Default
    @Column(nullable = false)
    private String thumbnailUrl = DEFAULT_IMAGE_URL;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @OneToOne(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    @Builder.Default
    @ElementCollection
    @CollectionTable(
            name = "service_item_image",
            joinColumns = @JoinColumn(name = "service_item_id")
    )
    @Column(name = "image_id")
    private List<Long> imageIds = new ArrayList<>();

    public void addImageId(Long id) {
        imageIds.add(id);
    }

    public boolean hasImage() {
        return imageIds.isEmpty();
    }


    public void participate() {
        if (this.isFull()) {
            throw new ConflictException(ErrorCode.MEMBER_FULL);
        }
        this.currentMember++;

        if (this.status == ServiceStatus.RECRUITING && isReachedMinimum()) {
            this.status = ServiceStatus.SUCCEEDED;
        }

        if (isFull()) {
            this.status = ServiceStatus.CLOSED;
        }
    }

    public boolean isReachedMinimum() {
        return this.currentMember >= this.minimumMember;
    }

    public boolean isFull() {
        return this.currentMember >= this.maximumMember;
    }

    public void finish() {
        this.status = ServiceStatus.CLOSED;
    }

    // 서비스아이템 등록을 위한 검증
    public void validateAppliable(LocalDateTime now) {
        // 삭제 여부
        if (this.isDeleted) {
            throw new ConflictException(ErrorCode.SERVICE_DELETED);
        }
        // 안되는 상황 구체화
        switch (this.status) {
            case FAILED -> throw new ConflictException(ErrorCode.SERVICE_RECRUITMENT_FAILED);
            case CANCELED -> throw new ConflictException(ErrorCode.SERVICE_RECRUITMENT_CANCELED);
            case COMPLETED -> throw new ConflictException(ErrorCode.SERVICE_ALREADY_COMPLETED);
            case CLOSED -> throw new ConflictException(ErrorCode.MEMBER_FULL);
        }

        // 마감 기한 확인
        if (now.isAfter(this.deadline)) {
            throw new ConflictException(ErrorCode.SERVICE_DEADLINE_EXPIRED);
        }

        if (this.currentMember >= this.maximumMember) {
            throw new ConflictException(ErrorCode.MEMBER_FULL);
        }
    }

    public boolean isOwner(Long companyId) {
        return this.company.getId().equals(companyId);
    }

    public void delete() {
        this.status = ServiceStatus.CANCELED;
        this.isDeleted = true;
    }
}
