package teamssavice.ssavice.wish.controller.dto;

import lombok.Builder;
import teamssavice.ssavice.address.AddressResponse;
import teamssavice.ssavice.serviceItem.constants.ServiceCategory;
import teamssavice.ssavice.wish.service.dto.WishModel;

import java.time.LocalDateTime;

public class WishResponse {

    @Builder
    public record Summary(
            Long serviceId,
            String thumbnailUrl,
            ServiceCategory category,
            Long companyId,
            String companyName,
            String title,
            AddressResponse.RegionSummary region,
            Long currentMember,
            Long minimumMember,
            Long maximumMember,
            Long basePrice,
            Integer discountRatio,
            Long discountedPrice,
            LocalDateTime deadline,
            String tag
    ) {
        public static Summary from(WishModel.Summary model) {
            return Summary.builder()
                    .serviceId(model.serviceId())
                    .thumbnailUrl(model.thumbnailUrl())
                    .category(model.category())
                    .companyId(model.companyId())
                    .companyName(model.companyName())
                    .title(model.title())
                    .region(AddressResponse.RegionSummary.from(model.region()))
                    .currentMember(model.currentMember())
                    .minimumMember(model.minimumMember())
                    .maximumMember(model.maximumMember())
                    .basePrice(model.basePrice())
                    .discountRatio(model.discountRatio())
                    .discountedPrice(model.discountedPrice())
                    .deadline(model.deadline())
                    .tag(model.tag())
                    .build();
        }
    }


}
