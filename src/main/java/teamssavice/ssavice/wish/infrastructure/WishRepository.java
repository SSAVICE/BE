package teamssavice.ssavice.wish.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import teamssavice.ssavice.wish.entity.Wish;

import java.util.Optional;

public interface WishRepository extends JpaRepository<Wish, Long> {

    Optional<Wish> findByUserIdAndServiceItemId(Long userId, Long serviceId);
}
