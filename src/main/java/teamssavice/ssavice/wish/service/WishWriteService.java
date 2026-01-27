package teamssavice.ssavice.wish.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;
import teamssavice.ssavice.user.entity.Users;
import teamssavice.ssavice.wish.entity.Wish;
import teamssavice.ssavice.wish.infrastructure.WishRepository;


@Service
@RequiredArgsConstructor
@Transactional
public class WishWriteService {

    private final WishRepository wishRepository;

    @Transactional
    public void addWish(Users user, ServiceItem serviceItem) {
        wishRepository.save(Wish.create(user, serviceItem));
    }

    @Transactional
    public void remove(Wish wish) {
        wishRepository.delete(wish);
    }
}
