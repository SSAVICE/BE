package teamssavice.ssavice.serviceItem.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import teamssavice.ssavice.company.entity.Company;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;

import java.util.List;

public interface ServiceItemRepository extends JpaRepository<ServiceItem, Long> {

    List<ServiceItem> findTop5ByCompanyOrderByDeadlineDesc(Company company);
}
