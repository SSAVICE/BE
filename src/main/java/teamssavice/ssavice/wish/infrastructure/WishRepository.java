package teamssavice.ssavice.wish.infrastructure;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import teamssavice.ssavice.wish.entity.Wish;

import java.util.Optional;

public interface WishRepository extends JpaRepository<Wish, Long> {

    Optional<Wish> findByUserIdAndServiceItemId(Long userId, Long serviceId);

    @Query(value = "select w from Wish w " +
            "join fetch w.serviceItem s " +
            "join fetch s.company c " +
            "join fetch s.address a " +
            "where w.user.id = :userId " +
            "order by w.id desc",
            countQuery = "select count(w) from Wish w where w.user.id = :userId")
    Page<Wish> findAllByUserId(Long userId, Pageable pageable);
}
