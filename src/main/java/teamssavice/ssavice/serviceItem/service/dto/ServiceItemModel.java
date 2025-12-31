package teamssavice.ssavice.serviceItem.service.dto;

import lombok.Builder;
import teamssavice.ssavice.address.Address;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;
import teamssavice.ssavice.serviceItem.constants.ServiceStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    public record ItemInfo(
            Long id,
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
            String category,
            String tag,
            Long currentMember,

            Long minimumMember,
            Long maximumMember,

            String address,
            String detailAddress,
            BigDecimal latitude,
            BigDecimal longitude,
            String region1Code,
            String region2Code
    ) {
        public static ItemInfo from (ServiceItem entity) {
            Address addr = entity.getAddress();

            return ItemInfo.builder()
                    .id(entity.getId())
                    .companyId(entity.getCompany().getId())
                    .addressId(addr.getId())
                    .companyName(entity.getCompany().getCompanyName())
                    .title(entity.getTitle())
                    .description(entity.getDescription())
                    .basePrice(entity.getPrice().getBasePrice())
                    .discountRate(entity.getPrice().getDiscountRate())
                    .discountedPrice(entity.getPrice().getDiscountedPrice())
                    .status(entity.getStatus())
                    .startDate(entity.getStartDate())
                    .endDate(entity.getEndDate())
                    .deadline(entity.getDeadline())
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
                    .region1Code(addr.getRegion1Code())
                    .region2Code(addr.getRegion2Code())
                    .build();
        }
    }
}