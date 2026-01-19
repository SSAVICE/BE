package teamssavice.ssavice.serviceItem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
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
@Builder(toBuilder = true)
@AllArgsConstructor
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

    public ServiceStatus getStatus() {
        if(this.isDeleted) return ServiceStatus.CANCELED;
        if(isInUse()) return ServiceStatus.IN_USE;
        if(isCompleted()) return ServiceStatus.COMPLETED;
        if(isFull()) return ServiceStatus.FULLED;
        if (isTimeOver()) {
            if(!isReachedMinimum()) return ServiceStatus.FAILED;
            return ServiceStatus.FULLED;
        }
        if(isReachedMinimum()) return ServiceStatus.SUCCEEDED;
        return ServiceStatus.RECRUITING;
    }

    public void participate() {
        if (this.isFull()) {
            throw new ConflictException(ErrorCode.MEMBER_FULL);
        }
        this.currentMember++;
    }

    public boolean isReachedMinimum() {
        return this.currentMember >= this.minimumMember;
    }

    public boolean isFull() {
        return this.currentMember >= this.maximumMember;
    }

    // 이용 종료 여부
    public boolean isTimeOver() {
        return this.deadline.isBefore(LocalDateTime.now());
    }

    // 이용중인지 여부
    public boolean isInUse() {
        LocalDateTime now = LocalDateTime.now();
        return isReachedMinimum() && (now.isAfter(startDate) || now.isEqual(startDate)) && now.isBefore(endDate);
    }

    public boolean isCompleted() {
        return isReachedMinimum() && LocalDateTime.now().isAfter(endDate);
    }

    // 서비스아이템 등록을 위한 검증
    public void validateAppliable() {
        // 삭제 여부
        ServiceStatus status = getStatus();
        // 안되는 상황 구체화
        switch (status) {
            case FAILED -> throw new ConflictException(ErrorCode.SERVICE_RECRUITMENT_FAILED);
            case CANCELED -> throw new ConflictException(ErrorCode.SERVICE_RECRUITMENT_CANCELED);
            case COMPLETED -> throw new ConflictException(ErrorCode.SERVICE_ALREADY_COMPLETED);
            case FULLED -> throw new ConflictException(ErrorCode.MEMBER_FULL);
        }


        // 마감 기한 확인
        if (LocalDateTime.now().isAfter(this.deadline)) {
            throw new ConflictException(ErrorCode.SERVICE_DEADLINE_EXPIRED);
        }
    }
}
