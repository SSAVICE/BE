package teamssavice.ssavice.wish.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.s3.S3Service;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;
import teamssavice.ssavice.serviceItem.service.ServiceItemReadService;
import teamssavice.ssavice.user.entity.Users;
import teamssavice.ssavice.user.service.UserReadService;
import teamssavice.ssavice.wish.entity.Wish;
import teamssavice.ssavice.wish.service.dto.WishCommand;
import teamssavice.ssavice.wish.service.dto.WishModel;


@Service
@RequiredArgsConstructor
public class WishService {

    private final WishWriteService wishWriteService;
    private final WishReadService wishReadService;
    private final UserReadService userReadService;
    private final ServiceItemReadService serviceItemReadService;
    private final S3Service s3Service;

    @Transactional
    public void updateWishStatus(WishCommand.UpdateStatus command) {
        if (command.targetStatus()) {
            Users user = userReadService.getReferenceById(command.userId());
            ServiceItem serviceItem = serviceItemReadService.getReferenceById(command.serviceId());

            wishWriteService.addWish(user, serviceItem);
        } else {
            wishReadService.findByUserIdAndServiceId(command.userId(), command.serviceId())
                .ifPresent(wishWriteService::remove);
        }
    }

    @Transactional(readOnly = true)
    public Page<WishModel.Summary> getWishList(WishCommand.Retrieve command) {
        Page<Wish> wishPage = wishReadService.getWishList(command);

        return wishPage.map(wish -> {
            ServiceItem serviceItem = wish.getServiceItem();
            String presignedUrl = s3Service.generateGetPresignedUrl(serviceItem.getObjectKey());
            return WishModel.Summary.from(wish, presignedUrl);
        });
    }

}
