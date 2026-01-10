package teamssavice.ssavice.serviceItem.service.dto;

import lombok.Builder;
import org.springframework.data.domain.Pageable;
import teamssavice.ssavice.imageresource.ImageRequest;
import teamssavice.ssavice.serviceItem.controller.dto.ServiceItemRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ServiceItemCommand {

    public record Create(
            Long companyId,
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
            String regionCode,
            BigDecimal longitude,
            BigDecimal latitude,
            String postCode,
            String address,
            String detailAddress,
            List<String> imageObjectKeys
    ) {

        public static ServiceItemCommand.Create from(Long companyId, ServiceItemRequest.Create request) {
            List<String> objectKeys = request.imageConfirms().stream().map(ImageRequest.Confirm::objectKey).toList();
            return new ServiceItemCommand.Create(
                    companyId,
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
                    request.region().regionCode(),
                    request.region().longitude(),
                    request.region().latitude(),
                    request.region().postCode(),
                    request.region().address(),
                    request.region().detailAddress(),
                    objectKeys
            );
        }
    }

    @Builder
    public record Search (
        String category,
        String query,
        String gugun,
        String region,
        Integer range,
        Long minPrice,
        Long maxPrice,
        Integer sortBy,
        Long lastId,      // 커서 ID
        Pageable pageable
    ) {
        public static Search of(ServiceItemRequest.Search request, Pageable pageable) {
            return new Search(
                    request.getCategory(),
                    request.getQuery(),
                    request.getGugun(),
                    request.getRegion(),
                    request.getRange(),
                    request.getMinPrice(),
                    request.getMaxPrice(),
                    request.getSortBy(),
                    request.getLastId(),
                    pageable
            );
        }
    }
}
