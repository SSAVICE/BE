package teamssavice.ssavice.serviceItem.infrastructure.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import teamssavice.ssavice.serviceItem.constants.ServiceStatus;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;

public interface ServiceItemRepository extends JpaRepository<ServiceItem, Long>,
    ServiceItemRepositoryCustom {

    @Query("SELECT s FROM ServiceItem s " +
        "JOIN FETCH s.address " +
        "LEFT JOIN FETCH s.thumbnailImageResource " +
        "WHERE s.company.id = :companyId " +
        "ORDER BY s.deadline DESC")
    List<ServiceItem> findTop5ByCompanyIdOrderByDeadlineDesc(Long companyId, Pageable pageable);

    @Query("SELECT s FROM ServiceItem s " +
        "LEFT JOIN FETCH s.thumbnailImageResource " +
        "WHERE s.company.id = :companyId")
    Page<ServiceItem> findAllByCompanyId(Long companyId, Pageable pageable);

    @Query("SELECT s FROM ServiceItem s " +
        "LEFT JOIN FETCH s.thumbnailImageResource " +
        "WHERE s.company.id = :companyId " +
        "AND s.status = :status " +
        "AND s.deadline > :now")
    Page<ServiceItem> findAllByCompanyIdAndStatus(Long companyId, ServiceStatus status,
        LocalDateTime now, Pageable pageable);
}
