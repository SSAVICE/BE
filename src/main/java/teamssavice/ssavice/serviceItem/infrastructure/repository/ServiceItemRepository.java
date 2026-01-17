package teamssavice.ssavice.serviceItem.infrastructure.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import teamssavice.ssavice.company.entity.Company;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;

import java.time.LocalDateTime;
import java.util.List;

public interface ServiceItemRepository extends JpaRepository<ServiceItem, Long>, ServiceItemRepositoryCustom {

    List<ServiceItem> findTop5ByCompanyOrderByDeadlineDesc(Company company);

    Page<ServiceItem> findAllByCompany_Id(Long companyId, Pageable pageable);

    @Query("SELECT s FROM ServiceItem s " +
            "WHERE s.company.id = :companyId " +
            "AND s.isDeleted = false " +
            "AND s.deadline > :now " +
            "AND s.currentMember < s.maximumMember")
    Page<ServiceItem> findAllRecruitingByCompany_Id(Long companyId, LocalDateTime now, Pageable pageable);
}
