package teamssavice.ssavice.serviceItem.service.dto;

import teamssavice.ssavice.serviceItem.controller.dto.ServiceItemRequest;

import java.math.BigDecimal;
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
            String tag,
            BigDecimal longitude,
            BigDecimal latitude,
            String postCode,
            String address,
            String detailAddress
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
                    request.tag(),
                    request.longitude(),
                    request.latitude(),
                    request.postCode(),
                    request.address(),
                    request.detailAddress()
            );
        }
    }

}
