package teamssavice.ssavice.wish.service;


import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.wish.entity.Wish;
import teamssavice.ssavice.wish.infrastructure.WishRepository;
import teamssavice.ssavice.wish.service.dto.WishCommand;

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
    public Page<Wish> getWishList(WishCommand.Retrieve command) {

        return wishRepository.findAllByUserId(command.userId(), command.pageable());
    }

    @Transactional(readOnly = true)
    public boolean existsByUserIdAndServiceItemId(@Nullable Long userId, Long serviceId) {
        if(userId == null) return false;
        return wishRepository.existsByUserIdAndServiceItemId(userId, serviceId);
    }

}
