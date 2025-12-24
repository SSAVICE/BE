package teamssavice.ssavice.serviceItem.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;

public interface ServiceItemRepository extends JpaRepository<ServiceItem, Long> {
}
