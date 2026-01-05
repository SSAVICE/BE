package teamssavice.ssavice.serviceItem.controller.dto;

import lombok.Builder;
import teamssavice.ssavice.serviceItem.constants.ServiceStatus;
import teamssavice.ssavice.serviceItem.service.dto.ServiceItemModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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

    @Builder
    public record Search(
            Long serviceId,
            String serviceImageUrl,
            String category,
            String title,
            String tag,
            String status,

            Long companyId,
            String companyName,

            BigDecimal latitude,
            BigDecimal longitude,
            String region1,
            String region2,

            Long currentMember,
            Long minimumMember,
            Long maximumMember,

            Long basePrice,
            Integer discountRatio,
            Long discountedPrice,

            LocalDateTime deadline
    ) {
        public static Search from(ServiceItemModel.Search model) {
            return ServiceItemResponse.Search.builder()
                    .serviceId(model.serviceId())
                    .serviceImageUrl(null) // 이미지는 28 이슈에서 해결해서 그거 기본 URL 쓰면서 지우면될듯
                    .category(model.category())
                    .title(model.title())
                    .tag(model.tag())
                    .status(model.status().name())
                    .companyId(model.companyId())
                    .companyName(model.companyName())
                    .latitude(model.latitude())
                    .longitude(model.longitude())
                    .region1(model.region1())
                    .region2(model.region2())
                    .currentMember(model.currentMember())
                    .minimumMember(model.minimumMember())
                    .maximumMember(model.maximumMember())
                    .basePrice(model.basePrice())
                    .discountRatio(model.discountRatio())
                    .discountedPrice(model.discountedPrice())
                    .deadline(model.deadline())
                    .build();
        }
    }

    @Builder
    public record Detail(
            List<String> imageUrl,
            Long serviceId,
            String category,
            Long companyId,
            String title,
            String description,
            BigDecimal latitude,
            BigDecimal longitude,
            String region1,
            String region2,
            Long currentMember,
            Long minimumMember,
            Long maximumMember,
            Long basePrice,
            Integer discountRatio,
            Long discountedPrice,
            LocalDateTime deadline,
            String tag, //  태그도 ES 도입하면서 수정 예정 - 지금은 그냥 String으로
            LocalDateTime startDate,
            LocalDateTime endDate,
            Boolean isLiked,
            String status,
            LocalDateTime createdAt
    ) {
        public static Detail from(ServiceItemModel.Detail model) {
            return Detail.builder()
                    .imageUrl(model.imageUrl())
                    .serviceId(model.serviceId())
                    .category(model.category())
                    .companyId(model.companyId())
                    .title(model.title())
                    .description(model.description())
                    .latitude(model.latitude())
                    .longitude(model.longitude())
                    .region1(model.region1())
                    .region2(model.region2())
                    .currentMember(model.currentMember())
                    .minimumMember(model.minimumMember())
                    .maximumMember(model.maximumMember())
                    .basePrice(model.basePrice())
                    .discountRatio(model.discountRate())
                    .discountedPrice(model.discountedPrice())
                    .deadline(model.deadline())
                    .tag(model.tag())
                    .startDate(model.startDate())
                    .endDate(model.endDate())
                    .isLiked(model.liked())
                    .status(model.status().name())
                    .createdAt(model.createdAt())
                    .build();
        }
    }
}