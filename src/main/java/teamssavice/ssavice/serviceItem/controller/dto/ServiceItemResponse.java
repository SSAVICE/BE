package teamssavice.ssavice.serviceItem.controller.dto;

import lombok.Builder;
import teamssavice.ssavice.serviceItem.service.dto.ServiceItemModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ServiceItemResponse {

    public record Register(
            Long serviceId
    ) {
        public static Register from(ServiceItemModel.ItemInfo model) {
            return new Register(model.id());
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
        public static Search from(ServiceItemModel.ItemInfo model) {
            return Search.builder()
                    .serviceId(model.id())
                    .serviceImageUrl(null) // 이미지는 28 이슈에서 해결해서 그거 기본 URL 쓰면서 지우면될듯
                    .category(model.category())
                    .title(model.title())
                    .tag(model.tag())
                    .status(model.status().name())
                    .companyId(model.companyId())
                    .companyName(model.companyName())
                    .latitude(model.latitude())
                    .longitude(model.longitude())
                    .region1(model.region1Code())
                    .region2(model.region2Code())
                    .currentMember(model.currentMember())
                    .minimumMember(model.minimumMember())
                    .maximumMember(model.maximumMember())
                    .basePrice(model.basePrice())
                    .discountRatio(model.discountRate())
                    .discountedPrice(model.discountedPrice())
                    .deadline(model.deadline())
                    .build();
        }
    }
}