package teamssavice.ssavice.serviceItem.service.dto;

import lombok.Builder;
import org.springframework.data.domain.Pageable;

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
        Pageable pageable,
        boolean onSale
    ) {
    }

    @Builder
    public record RetrieveByCompanyAndOnSale(
        Long companyId,
        Pageable pageable,
        boolean onSale
    ) {
        public static RetrieveByCompanyAndOnSale of(Long companyId, Pageable pageable, Boolean onSale) {
            return RetrieveByCompanyAndOnSale.builder()
                    .companyId(companyId)
                    .pageable(pageable)
                    .onSale(Boolean.TRUE.equals(onSale))
                    .build();
        }
    }
}
