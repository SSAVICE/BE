package teamssavice.ssavice.serviceItem.infrastructure.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import teamssavice.ssavice.company.entity.Company;
import teamssavice.ssavice.serviceItem.constants.ServiceStatus;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;

import java.util.List;

public interface ServiceItemRepository extends JpaRepository<ServiceItem, Long>, ServiceItemRepositoryCustom {

    List<ServiceItem> findTop5ByCompanyOrderByDeadlineDesc(Company company);

    Page<ServiceItem> findAllByCompany_IdAndStatus(Long companyId, ServiceStatus status, Pageable pageable);
}
