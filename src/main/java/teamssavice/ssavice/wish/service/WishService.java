package teamssavice.ssavice.wish.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.wish.entity.Wish;
import teamssavice.ssavice.wish.service.dto.WishCommand;
import teamssavice.ssavice.wish.service.dto.WishModel;


@Service
@RequiredArgsConstructor
public class WishService {

    private final WishWriteService wishWriteService; // 실제 쓰기 로직 담당
    private final WishReadService wishReadService;

    @Transactional
    public void updateWishStatus(WishCommand.UpdateStatus command) {

        wishWriteService.updateWishStatus(command.userId(), command.serviceId(), command.targetStatus());
    }

    @Transactional(readOnly = true)
    public Page<WishModel.Summary> getWishList(WishCommand.Retrieve command) {
        return wishReadService.getWishList(command);
    }

}
