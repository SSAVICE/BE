package teamssavice.ssavice.serviceItem.infrastructure.opensearch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceItemSearchDocument {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private Long id;
    private String title;
    private String description;
    private String category;
    private List<String> tags;
    private String status;
    private Boolean isDeleted;

    private Long basePrice;
    private Integer discountRate;
    private Long discountedPrice;

    private Long currentMember;
    private Long maximumMember;
    private Long minimumMember;

    private Long companyId;
    private String companyName;

    private String address;
    private String detailAddress;
    private String region;
    private String gugun;

    private String startDate;
    private String endDate;
    private String deadline;
    private String createdAt;


    private GeoLocation location;

    public static ServiceItemSearchDocument from(ServiceItem item) {
        GeoLocation geo = GeoLocation.builder()
                .lat(item.getAddress().getLatitude())
                .lon(item.getAddress().getLongitude())
                .build();

        return ServiceItemSearchDocument.builder()
                .id(item.getId())
                .title(item.getTitle())
                .description(item.getDescription())
                .category(item.getCategory())
                .tags(item.getTag() != null ? List.of(item.getTag().split(",")) : List.of())
                .status(item.getStatus().name())
                .isDeleted(item.isDeleted())
                .basePrice(item.getPrice().getBasePrice())
                .discountRate(item.getPrice().getDiscountRate())
                .discountedPrice(item.getPrice().getDiscountedPrice())
                .currentMember(item.getCurrentMember())
                .maximumMember(item.getMaximumMember())
                .minimumMember(item.getMinimumMember())
                .companyId(item.getCompany().getId())
                .companyName(item.getCompany().getCompanyName())
                .address(item.getAddress().getAddress())
                .detailAddress(item.getAddress().getDetailAddress())
                .region(item.getAddress().getRegion())
                .gugun(item.getAddress().getGugun())
                .startDate(item.getStartDate().format(DATE_FORMAT))
                .endDate(item.getEndDate().format(DATE_FORMAT))
                .deadline(item.getDeadline().format(DATE_FORMAT))
                .createdAt(item.getCreatedAt().format(DATE_FORMAT))
                .location(geo)
                .build();
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GeoLocation {
        private BigDecimal lat;
        private BigDecimal lon;
    }
}