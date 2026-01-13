package teamssavice.ssavice.serviceItem.service.dto;

import lombok.Builder;
import org.springframework.data.domain.Pageable;
import teamssavice.ssavice.serviceItem.constants.ServiceStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ServiceItemCommand {

    @Builder
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
    }

    @Builder
    public record RetrieveByCompanyAndStatus(
        Long companyId,
        Pageable pageable,
        ServiceStatus status
    ) {
        public static RetrieveByCompanyAndStatus of(Long companyId, Pageable pageable, ServiceStatus status) {
            return RetrieveByCompanyAndStatus.builder()
                    .companyId(companyId)
                    .pageable(pageable)
                    .status(status)
                    .build();
        }
    }
}
