package teamssavice.ssavice.serviceItem.infrastructure.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;

import java.util.List;

public interface ServiceItemRepository extends JpaRepository<ServiceItem, Long>, ServiceItemRepositoryCustom {

    @Query("SELECT s FROM ServiceItem s " +
            "JOIN FETCH s.address " +
            "WHERE s.company.id = :companyId " +
            "ORDER BY s.deadline DESC")
    List<ServiceItem> findTop5ByCompanyIdOrderByDeadlineDesc(Long companyId, Pageable pageable);
}