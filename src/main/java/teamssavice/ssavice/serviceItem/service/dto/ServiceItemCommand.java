package teamssavice.ssavice.serviceItem.service.dto;

import lombok.Builder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import teamssavice.ssavice.serviceItem.constants.ServiceStatusFilter;
import teamssavice.ssavice.serviceItem.constants.SortType;

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
        SortType sortType,
        Long lastId,      // 커서 ID
        List<String> searchAfter, // opensearch 전용인데 비교용으로 위필드와 같이 있지만 lastId는 삭제 예정
        Pageable pageable,
        boolean onSale,
        Long userId,
        BigDecimal userLatitude,
        BigDecimal userLongitude,
        Double distanceKm
    ) {
    }

    @Builder
    public record RetrieveByCompanyAndStatus(
        Long companyId,
        Pageable pageable,
        ServiceStatusFilter status
    ) {
        public static RetrieveByCompanyAndStatus of(Long companyId, Pageable pageable, ServiceStatusFilter status) {
            return RetrieveByCompanyAndStatus.builder()
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
    public record RetrieveByCompanyAndOnSale(
            Long companyId,
            Pageable pageable,
            boolean onSale
    ) {
        public static RetrieveByCompanyAndOnSale of(Long companyId, Pageable pageable, Boolean onSale) {
            return RetrieveByCompanyAndOnSale.builder()
                    .companyId(companyId)
                    .pageable(PageRequest.of(
                            pageable.getPageNumber(),
                            pageable.getPageSize(),
                            Sort.by("createdAt").descending()
                    ))
                    .onSale(Boolean.TRUE.equals(onSale))
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

    @Builder
    public record Nearby(
        BigDecimal userLatitude,
        BigDecimal userLongitude,
        BigDecimal latitude,
        BigDecimal longitude,
        int radiusMeters,
        int size,
        Long lastId
    ) {
        public static Nearby of(
            BigDecimal userLatitude,
            BigDecimal userLongitude,
            BigDecimal latitude,
            BigDecimal longitude,
            int radiusMeters,
            int size,
            Long lastId
        ) {
            return Nearby.builder()
                .userLatitude(userLatitude)
                .userLongitude(userLongitude)
                .latitude(latitude)
                .longitude(longitude)
                .radiusMeters(radiusMeters)
                .size(size)
                .lastId(lastId)
                .build();
        }
    }
}
