package teamssavice.ssavice.serviceItem.infrastructure.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import teamssavice.ssavice.serviceItem.constants.ServiceStatus;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;

import java.time.LocalDateTime;
import java.util.List;

public interface ServiceItemRepository extends JpaRepository<ServiceItem, Long>, ServiceItemRepositoryCustom {

    @Query("SELECT s FROM ServiceItem s " +
            "JOIN FETCH s.address " +
            "WHERE s.company.id = :companyId " +
            "ORDER BY s.deadline DESC")
    List<ServiceItem> findTop5ByCompanyIdOrderByDeadlineDesc(Long companyId, Pageable pageable);

    Page<ServiceItem> findAllByCompany_Id(Long companyId, Pageable pageable);

    @Query("SELECT s FROM ServiceItem s " +
            "WHERE s.company.id = :companyId " +
            "AND s.status = :status " +
            "AND s.deadline > :now")
    Page<ServiceItem> findAllByCompany_IdAndStatus(Long companyId, ServiceStatus status, LocalDateTime now, Pageable pageable);
}
