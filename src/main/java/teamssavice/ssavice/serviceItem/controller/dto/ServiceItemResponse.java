package teamssavice.ssavice.serviceItem.controller.dto;

import lombok.Builder;
import teamssavice.ssavice.serviceItem.constants.ServiceStatus;
import teamssavice.ssavice.serviceItem.service.dto.ServiceItemModel;

import java.time.LocalDateTime;

public class ServiceItemResponse {
    public record Register(
            Long serviceId
    ) {
        public static Register from(Long serviceId) {
            return new Register(serviceId);
        }
    }

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
        public static ServiceItemResponse.Summary from(ServiceItemModel.Summary model) {
            return Summary.builder()
                    .serviceId(model.serviceId())
                    .thumbnailUrl(model.thumbnailUrl())
                    .category(model.category())
                    .title(model.title())
                    .currentMember(model.currentMember())
                    .minimumMember(model.minimumMember())
                    .maximumMember(model.maximumMember())
                    .description(model.description())
                    .basePrice(model.basePrice())
                    .discountRate(model.discountRate())
                    .discountedPrice(model.discountedPrice())
                    .status(model.status())
                    .startDate(model.startDate())
                    .endDate(model.endDate())
                    .deadline(model.deadline())
                    .tag(model.tag())
                    .build();
        }
    }
}
