package teamssavice.ssavice.serviceItem.infrastructure.opensearch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.opensearch.client.opensearch._types.GeoLocation;

import java.util.List;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceItemSearchDocument {

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

    private Long thumbnailImageId;
    private String thumbnailObjectKey;

    private GeoLocation location;

    @Getter
    @NoArgsConstructor
    public static class GeoLocation {
        private Double lat;
        private Double lon;
    }
}