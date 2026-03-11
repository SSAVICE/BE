package teamssavice.ssavice.serviceItem.service.dto;

import lombok.Builder;
import teamssavice.ssavice.address.AddressModel;
import teamssavice.ssavice.serviceItem.constants.ServiceStatus;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;
import teamssavice.ssavice.serviceItem.infrastructure.opensearch.ServiceItemSearchDocument;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
            String tag,
            AddressModel.RegionSummary region
    ) {

        public static ServiceItemModel.Summary from(ServiceItem entity, String thumbnailUrl) {
            return Summary.builder()
                .serviceId(entity.getId())
                .thumbnailUrl(thumbnailUrl)
                .category(entity.getCategory().name())
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
                .region(AddressModel.RegionSummary.builder()
                    .gugun(entity.getAddress().getGugun())
                    .region(entity.getAddress().getRegion())
                    .latitude(entity.getAddress().getLatitude())
                    .longitude(entity.getAddress().getLongitude())
                    .build())
                .build();
        }
    }

    public record SearchContext(
            boolean isBooked,
            String imageUrl,
            double distanceKm,
            Long currentMember,
            ServiceStatus status
    ) {}


    @Builder
    public record Search(
            Long serviceId,
            String serviceImageUrl,
            String category,
            String title,
            String tag,
            ServiceStatus status,

            Long companyId,
            String companyName,

            AddressModel.RegionSummary region,

            Long currentMember,
            Long minimumMember,
            Long maximumMember,

            Long basePrice,
            Integer discountRatio,
            Long discountedPrice,

            LocalDateTime deadline,

        boolean isBooked,
        double distanceKm
    ) {
        // 기존 (QueryDSL용) - 나중에 삭제 예정
        public static Search from(ServiceItem entity, boolean isBooked, String imageUrl, double distanceKm) {
            return Search.builder()
                .serviceId(entity.getId())
                .companyId(entity.getCompany().getId())
                .companyName(entity.getCompany().getCompanyName())
                .serviceImageUrl(imageUrl)
                .title(entity.getTitle())
                .basePrice(entity.getPrice().getBasePrice())
                .discountRatio(entity.getPrice().getDiscountRate())
                .discountedPrice(entity.getPrice().getDiscountedPrice())
                .status(entity.getStatus())
                .deadline(entity.getDeadline())
                .category(entity.getCategory().name())
                .tag(entity.getTag())
                .currentMember(entity.getCurrentMember())
                .minimumMember(entity.getMinimumMember())
                .maximumMember(entity.getMaximumMember())
                .region(AddressModel.RegionSummary.builder()
                    .gugun(entity.getAddress().getGugun())
                    .region(entity.getAddress().getRegion())
                    .latitude(entity.getAddress().getLatitude())
                    .longitude(entity.getAddress().getLongitude())
                    .build())
                .isBooked(isBooked)
                .distanceKm(distanceKm)
                .build();
        }

        public static Search fromDocument(ServiceItemSearchDocument doc,  SearchContext context) {
            return Search.builder()
                    .serviceId(doc.getId())
                    .companyId(doc.getCompanyId())
                    .companyName(doc.getCompanyName())
                    .serviceImageUrl(context.imageUrl())
                    .title(doc.getTitle())
                    .basePrice(doc.getBasePrice())
                    .discountRatio(doc.getDiscountRate())
                    .discountedPrice(doc.getDiscountedPrice())
                    .status(context.status())
                    .deadline(parseDateTime(doc.getDeadline()))
                    .category(doc.getCategory())
                    .tag(doc.getTags() != null ? String.join(",", doc.getTags()) : null)
                    .currentMember(context.currentMember())
                    .minimumMember(doc.getMinimumMember())
                    .maximumMember(doc.getMaximumMember())
                    .region(AddressModel.RegionSummary.builder()
                            .gugun(doc.getGugun())
                            .region(doc.getRegion())
                            .latitude(doc.getLocation().getLat())
                            .longitude(doc.getLocation().getLon())
                            .build())
                    .isBooked(context.isBooked())
                    .distanceKm(context.distanceKm())
                    .build();
        }
    }

    private static LocalDateTime parseDateTime(String dateStr) {
        if (dateStr == null) return null;
        if (dateStr.length() == 10) {
            // "2026-02-25" → "2026-02-25T00:00:00"
            return LocalDate.parse(dateStr).atStartOfDay();
        }
        return LocalDateTime.parse(dateStr);
    }

    @Builder
    public record Detail(
            Long serviceId,
            Long companyId,
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
            LocalDateTime createdAt,
            String category,
            String tag,

            Long currentMember,
            Long minimumMember,
            Long maximumMember,

            AddressModel.RegionSummary region,

            List<String> imageUrl,
            Boolean liked,
            Boolean booked
    ) {
        public static Detail from (ServiceItem entity, List<String> imageUrl, boolean isLiked, boolean isBooked) {
            return Detail.builder()
                    .serviceId(entity.getId())
                    .companyId(entity.getCompany().getId())
                    .companyName(entity.getCompany().getCompanyName())
                    .imageUrl(imageUrl)
                    .title(entity.getTitle())
                    .description(entity.getDescription())
                    .basePrice(entity.getPrice().getBasePrice())
                    .discountRate(entity.getPrice().getDiscountRate())
                    .discountedPrice(entity.getPrice().getDiscountedPrice())
                    .status(entity.getStatus())
                    .startDate(entity.getStartDate())
                    .endDate(entity.getEndDate())
                    .deadline(entity.getDeadline())
                    .createdAt(entity.getCreatedAt())
                    .category(entity.getCategory().name())
                    .tag(entity.getTag())
                    .currentMember(entity.getCurrentMember())
                    .minimumMember(entity.getMinimumMember())
                    .maximumMember(entity.getMaximumMember())
                    // Address 관련
                    .region(AddressModel.RegionSummary.builder()
                            .gugun(entity.getAddress().getGugun())
                            .region(entity.getAddress().getRegion())
                            .latitude(entity.getAddress().getLatitude())
                            .longitude(entity.getAddress().getLongitude())
                            .build())
                    .liked(isLiked)
                    .booked(isBooked)
                    .build();
        }
    }

    @Builder
    public record Count(
            Long total,
            Long applying,
            Long completed
    ) {

        public static ServiceItemModel.Count from(Long applying, Long completed, Long total) {
            return Count.builder()
                    .applying(applying)
                    .completed(completed)
                    .total(total)
                    .build();
        }
    }

    @Builder
    public record Nearby(
            Long serviceId,
            String serviceImageUrl,
            String category,
            String title,
            String tag,
            ServiceStatus status,

            Long companyId,
            String companyName,

            AddressModel.RegionSummary region,

            Long currentMember,
            Long minimumMember,
            Long maximumMember,

            Long basePrice,
            Integer discountRatio,
            Long discountedPrice,

            LocalDateTime deadline,
            double distanceKm
    ) {
        public static Nearby from(ServiceItem entity, double distanceKm, String thumbnailUrl) {
            return Nearby.builder()
                    .serviceId(entity.getId())
                    .companyId(entity.getCompany().getId())
                    .companyName(entity.getCompany().getCompanyName())
                    .serviceImageUrl(thumbnailUrl)
                    .title(entity.getTitle())
                    .basePrice(entity.getPrice().getBasePrice())
                    .discountRatio(entity.getPrice().getDiscountRate())
                    .discountedPrice(entity.getPrice().getDiscountedPrice())
                    .status(entity.getStatus())
                    .deadline(entity.getDeadline())
                    .category(entity.getCategory().name())
                    .tag(entity.getTag())
                    .currentMember(entity.getCurrentMember())
                    .minimumMember(entity.getMinimumMember())
                    .maximumMember(entity.getMaximumMember())
                    .region(AddressModel.RegionSummary.builder()
                            .gugun(entity.getAddress().getGugun())
                            .region(entity.getAddress().getRegion())
                            .latitude(entity.getAddress().getLatitude())
                            .longitude(entity.getAddress().getLongitude())
                            .build())
                    .distanceKm(distanceKm)
                    .build();
        }
    }
}