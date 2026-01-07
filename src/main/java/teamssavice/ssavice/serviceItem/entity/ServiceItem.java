package teamssavice.ssavice.serviceItem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import teamssavice.ssavice.address.Address;
import teamssavice.ssavice.company.entity.Company;
import teamssavice.ssavice.global.entity.BaseEntity;
import teamssavice.ssavice.serviceItem.constants.ServiceStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
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

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private ServiceStatus status = ServiceStatus.BEFORE_RECRUITING;

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
}
