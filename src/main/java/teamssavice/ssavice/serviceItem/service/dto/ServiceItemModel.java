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

            String imageUrl, // 이미지 테이블 추가되면서 변경 예정
            Boolean liked
    ) {
        public static ItemInfo from (ServiceItem entity) {
            Address addr = entity.getAddress();

            return ItemInfo.builder()
                    .serviceId(entity.getId())
                    .companyId(entity.getCompany().getId())
                    .addressId(addr.getId())
                    .companyName(entity.getCompany().getCompanyName())
                    .imageUrl(entity.getThumbnailUrl())
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