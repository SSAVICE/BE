package teamssavice.ssavice.wish.service.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import teamssavice.ssavice.address.AddressModel;
import teamssavice.ssavice.serviceItem.constants.ServiceCategory;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;
import teamssavice.ssavice.wish.entity.Wish;

public class WishModel {

    @Builder
    public record Summary(
        Long serviceId,
        String thumbnailUrl,
        ServiceCategory category,
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

        public static Summary from(Wish wish, String thumbnailUrl) {
            ServiceItem item = wish.getServiceItem();

            return Summary.builder()
                .serviceId(item.getId())
                .thumbnailUrl(thumbnailUrl)
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
