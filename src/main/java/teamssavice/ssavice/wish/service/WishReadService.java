package teamssavice.ssavice.wish.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.wish.entity.Wish;
import teamssavice.ssavice.wish.infrastructure.WishRepository;
import teamssavice.ssavice.wish.service.dto.WishCommand;
import teamssavice.ssavice.wish.service.dto.WishModel;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WishReadService {

    private final WishRepository wishRepository;


    @Transactional(readOnly = true)
    public Optional<Wish> findByUserIdAndServiceId(Long userId, Long serviceId) {
        return wishRepository.findByUserIdAndServiceItemId(userId, serviceId);
    }

    @Transactional(readOnly = true)
    public Page<WishModel.Summary> getWishList(WishCommand.Retrieve command) {

        Page<Wish> wishPage = wishRepository.findAllByUserId(command.userId(), command.pageable());
        return wishPage.map(WishModel.Summary::from);
    }

    @Transactional(readOnly = true)
    public boolean existsByUserIdAndServiceItemId(Long userId, Long serviceId) {

        return wishRepository.existsByUserIdAndServiceItemId(userId, serviceId);
    }

}
