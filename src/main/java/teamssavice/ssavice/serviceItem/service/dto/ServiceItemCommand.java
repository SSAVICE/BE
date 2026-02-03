package teamssavice.ssavice.serviceItem.service.dto;

import lombok.Builder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import teamssavice.ssavice.serviceItem.constants.ServiceStatusFilter;

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
    public record RetrieveByCompany(
        Long companyId,
        Pageable pageable,
        ServiceStatusFilter status
    ) {
        public static RetrieveByCompany of(Long companyId, Pageable pageable, ServiceStatusFilter status) {
            return RetrieveByCompany.builder()
                    .companyId(companyId)
                    .pageable(PageRequest.of(
                            pageable.getPageNumber(),
                            pageable.getPageSize(),
                            Sort.by("createdAt").descending()
                    ))
                    .status(status)
                    .build();
        }
    }

    @Builder
    public record Delete(
            Long companyId,
            Long serviceId
    ) {
        public static Delete of(Long companyId, Long serviceId) {
            return Delete.builder()
                    .companyId(companyId)
                    .serviceId(serviceId)
                    .build();
        }
    }

    @Builder
    public record Cancel(
            Long userId,
            Long serviceId
    ) {
        public static Cancel of(Long userId, Long serviceId) {
            return Cancel.builder()
                    .userId(userId)
                    .serviceId(serviceId)
                    .build();
        }
    }
}
