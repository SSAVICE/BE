package teamssavice.ssavice.serviceItem.service.dto;

import lombok.Builder;
import teamssavice.ssavice.address.Address;
import teamssavice.ssavice.serviceItem.constants.ServiceStatus;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ServiceItemModel {

    @Builder
    public record Summary(
            Long serviceId,
            String thumbnailUrl,
            String category,
            String title,
            Long currentMember,
            Long minimumMember,
            Long maximumMember,
            String description,
            Long basePrice,
            Integer discountRate,
            Long discountedPrice,
            ServiceStatus status,
            LocalDateTime startDate,
            LocalDateTime endDate,
            LocalDateTime deadline,
            String tag
    ) {
        public static ServiceItemModel.Summary from (ServiceItem entity) {
            return Summary.builder()
                    .serviceId(entity.getId())
                    .thumbnailUrl(entity.getThumbnailUrl())
                    .category(entity.getCategory())
                    .title(entity.getTitle())
                    .currentMember(entity.getCurrentMember())
                    .minimumMember(entity.getMinimumMember())
                    .maximumMember(entity.getMaximumMember())
                    .description(entity.getDescription())
                    .basePrice(entity.getPrice().getBasePrice())
                    .discountRate(entity.getPrice().getDiscountRate())
                    .discountedPrice(entity.getPrice().getDiscountedPrice())
                    .status(entity.getStatus())
                    .startDate(entity.getStartDate())
                    .endDate(entity.getEndDate())
                    .deadline(entity.getDeadline())
                    .tag(entity.getTag())
                    .build();
        }
    }

    @Builder
    public record Search(
            Long serviceId,
            String serviceImageUrl,
            String category,
            String title,
            String tag,
            ServiceStatus status,

            Long companyId,
            String companyName,

            BigDecimal latitude,
            BigDecimal longitude,
            String region1,
            String region2,

            Long currentMember,
            Long minimumMember,
            Long maximumMember,

            Long basePrice,
            Integer discountRatio,
            Long discountedPrice,

            LocalDateTime deadline
    ) {
        public static Search from (ServiceItem entity) {
            Address addr = entity.getAddress();

            return Search.builder()
                    .serviceId(entity.getId())
                    .companyId(entity.getCompany().getId())
                    .companyName(entity.getCompany().getCompanyName())
                    .serviceImageUrl(entity.getThumbnailUrl())
                    .title(entity.getTitle())
                    .basePrice(entity.getPrice().getBasePrice())
                    .discountRatio(entity.getPrice().getDiscountRate())
                    .discountedPrice(entity.getPrice().getDiscountedPrice())
                    .status(entity.getStatus())
                    .deadline(entity.getDeadline())
                    .category(entity.getCategory())
                    .tag(entity.getTag())
                    .currentMember(entity.getCurrentMember())
                    .minimumMember(entity.getMinimumMember())
                    .maximumMember(entity.getMaximumMember())
                    .latitude(addr.getLatitude())
                    .longitude(addr.getLongitude())
                    .region1(addr.getRegion1())
                    .region2(addr.getRegion2())
                    .build();
        }
    }

    @Builder
    public record Detail(
            Long serviceId,
            Long companyId,
            Long addressId,
            String companyName,
            String title,
            String description,
            Long basePrice,
            Integer discountRate,
            Long discountedPrice,
            ServiceStatus status,
            LocalDateTime startDate,
            LocalDateTime endDate,
            LocalDateTime deadline,
            LocalDateTime createdAt,
            String category,
            String tag,
            Long currentMember,

            Long minimumMember,
            Long maximumMember,

            String address,
            String detailAddress,
            BigDecimal latitude,
            BigDecimal longitude,
            String region1,
            String region2,
            String region1Code,
            String region2Code,

            List<String> imageUrl,
            Boolean liked
    ) {
        public static Detail from (ServiceItem entity, List<String> imageUrl) {
            Address addr = entity.getAddress();

            return Detail.builder()
                    .serviceId(entity.getId())
                    .companyId(entity.getCompany().getId())
                    .addressId(addr.getId())
                    .companyName(entity.getCompany().getCompanyName())
                    .imageUrl(imageUrl)
                    .title(entity.getTitle())
                    .description(entity.getDescription())
                    .basePrice(entity.getPrice().getBasePrice())
                    .discountRate(entity.getPrice().getDiscountRate())
                    .discountedPrice(entity.getPrice().getDiscountedPrice())
                    .status(entity.getStatus())
                    .startDate(entity.getStartDate())
                    .endDate(entity.getEndDate())
                    .deadline(entity.getDeadline())
                    .createdAt(entity.getCreatedAt())
                    .category(entity.getCategory())
                    .tag(entity.getTag())
                    .currentMember(entity.getCurrentMember())
                    .minimumMember(entity.getMinimumMember())
                    .maximumMember(entity.getMaximumMember())
                    // Address 관련
                    .address(addr.getAddress())
                    .detailAddress(addr.getDetailAddress())
                    .latitude(addr.getLatitude())
                    .longitude(addr.getLongitude())
                    .region1(addr.getRegion1())
                    .region2(addr.getRegion2())
                    .region1Code(addr.getRegion1Code())
                    .region2Code(addr.getRegion2Code())
                    .liked(false) // wish 도입하면서 유저 별 조회 로직 추가 예정
                    .build();
        }
    }
}