package teamssavice.ssavice.serviceItem.service.dto;

import lombok.Builder;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;
import teamssavice.ssavice.serviceItem.constants.ServiceStatus;

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

}
