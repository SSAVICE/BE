package teamssavice.ssavice.review.infrastructure.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import teamssavice.ssavice.review.entity.Review;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query(
        value = "SELECT r FROM Review r " +
                "JOIN FETCH r.user " +
                "JOIN FETCH r.serviceItem " +
                "WHERE r.companyId = :companyId",
        countQuery = "SELECT COUNT(r) FROM Review r WHERE r.companyId = :companyId")
    Page<Review> findByCompanyId(Long companyId, Pageable pageable);

    @Query(
        value = "SELECT r FROM Review r " +
                "JOIN FETCH r.user " +
                "JOIN FETCH r.serviceItem " +
                "WHERE r.user.id = :userId",
        countQuery = "SELECT COUNT(r) FROM Review r WHERE r.user.id = :userId")
    Page<Review> findByUser_Id(Long userId, Pageable pageable);

    @Query("SELECT r FROM Review r " +
            "JOIN FETCH r.user " +
            "JOIN FETCH r.serviceItem " +
            "WHERE r.companyId = :companyId " +
            "order by r.createdAt desc")
    List<Review> findTop3ByCompanyIdOrderByCreatedAtDesc(Long companyId, Pageable pageable);
}
