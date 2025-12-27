package teamssavice.ssavice.review.infrastructure.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import teamssavice.ssavice.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByCompanyId(Long companyId, Pageable pageable);
}
