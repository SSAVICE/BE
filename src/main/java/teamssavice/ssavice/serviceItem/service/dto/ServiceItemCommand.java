package teamssavice.ssavice.serviceItem.service.dto;

import teamssavice.ssavice.company.entity.Company;
import teamssavice.ssavice.serviceItem.controller.dto.ServiceItemRequest;
import teamssavice.ssavice.serviceItem.entity.Price;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;

import java.time.LocalDateTime;

public class ServiceItemCommand {

    public record Create(
            Long userId,
            String title,
            String description,
            Long basePrice,
            Integer discountRate,
            Long minimumMember,
            Long maximumMember,
            LocalDateTime startDate,
            LocalDateTime endDate,
            LocalDateTime deadline,
            String category,
            String tag
    ) {

        public static Create from(Long userId, ServiceItemRequest.Create request) {
            return new Create(
                    userId,
                    request.title(),
                    request.description(),
                    request.basePrice(),
                    request.discountRate(),
                    request.minimumMember(),
                    request.maximumMember(),
                    request.startDate(),
                    request.endDate(),
                    request.deadline(),
                    request.category(),
                    request.tag()
            );
        }

        public ServiceItem toEntity(Company company) {
            return ServiceItem.builder()
                    .company(company)
                    .title(this.title)
                    .description(this.description)
                    .price(Price.of(this.basePrice, this.discountRate))
                    .minimumMember(this.minimumMember)
                    .maximumMember(this.maximumMember)
                    .startDate(this.startDate)
                    .endDate(this.endDate)
                    .deadline(this.deadline)
                    .category(this.category)
                    .tag(this.tag)
                    .build();
        }
    }

}
