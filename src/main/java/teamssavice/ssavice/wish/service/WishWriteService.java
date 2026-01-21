package teamssavice.ssavice.wish.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;
import teamssavice.ssavice.serviceItem.service.ServiceItemReadService;
import teamssavice.ssavice.user.entity.Users;
import teamssavice.ssavice.user.service.UserReadService;
import teamssavice.ssavice.wish.entity.Wish;
import teamssavice.ssavice.wish.infrastructure.WishRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class WishWriteService {

    private final WishRepository wishRepository;
    private final UserReadService userReadService;
    private final ServiceItemReadService serviceItemReadService;

    @Transactional
    public void updateWishStatus(Long userId, Long serviceItemId, boolean targetStatus) {

        Optional<Wish> wish = wishRepository.findByUserIdAndServiceItemId(userId, serviceItemId);

        if (targetStatus) {
            if (wish.isEmpty()) {
                Users user = userReadService.getReferenceById(userId);
                ServiceItem serviceItem = serviceItemReadService.getReferenceById(serviceItemId);
                wishRepository.save(Wish.create(user, serviceItem));
            }
        } else {
            wish.ifPresent(wishRepository::delete);
        }
    }
}
