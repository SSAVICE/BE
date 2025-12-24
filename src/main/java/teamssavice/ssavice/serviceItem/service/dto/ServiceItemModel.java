package teamssavice.ssavice.serviceItem.service.dto;

import teamssavice.ssavice.serviceItem.entity.ServiceItem;
import teamssavice.ssavice.serviceItem.entity.ServiceStatus;

import java.time.LocalDateTime;

public class ServiceItemModel {

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
            Long currentMember
    ) {
        public static ItemInfo from (ServiceItem entity) {
            return new ItemInfo(
                    entity.getId(),
                    entity.getCompany().getId(),
                    entity.getAddress().getId(),
                    entity.getCompany().getCompanyName(),
                    entity.getTitle(),
                    entity.getDescription(),
                    entity.getPrice().getBasePrice(),
                    entity.getPrice().getDiscountRate(),
                    entity.getPrice().getDiscountedPrice(),
                    entity.getStatus(),
                    entity.getStartDate(),
                    entity.getEndDate(),
                    entity.getDeadline(),
                    entity.getCategory(),
                    entity.getTag(),
                    entity.getCurrentMember()
            );
        }
    }


}
