package teamssavice.ssavice.wish.service.dto;

import lombok.Builder;
import teamssavice.ssavice.address.AddressModel;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;
import teamssavice.ssavice.wish.entity.Wish;

import java.time.LocalDateTime;

public class WishModel {

    @Builder
    public record Summary(
            Long serviceId,
            String thumbnailUrl,
            String category,
            Long companyId,
            String companyName,
            String title,
            AddressModel.RegionSummary region,
            Long currentMember,
            Long minimumMember,
            Long maximumMember,
            Long basePrice,
            Integer discountRatio,
            Long discountedPrice,
            LocalDateTime deadline,
            String tag,
            String status
    ) {
        public static Summary from(Wish wish) {
            ServiceItem item = wish.getServiceItem();

            return Summary.builder()
                    .serviceId(item.getId())
                    .thumbnailUrl(item.getThumbnailUrl())
                    .category(item.getCategory())
                    .companyId(item.getCompany().getId())
                    .companyName(item.getCompany().getCompanyName())
                    .title(item.getTitle())
                    .region(AddressModel.RegionSummary.from(item.getAddress()))
                    .currentMember(item.getCurrentMember())
                    .minimumMember(item.getMinimumMember())
                    .maximumMember(item.getMaximumMember())
                    .basePrice(item.getPrice().getBasePrice())
                    .discountRatio(item.getPrice().getDiscountRate())
                    .discountedPrice(item.getPrice().getDiscountedPrice())
                    .deadline(item.getDeadline())
                    .tag(item.getTag())
                    .status(item.getStatus().name())
                    .build();
        }
    }
}
