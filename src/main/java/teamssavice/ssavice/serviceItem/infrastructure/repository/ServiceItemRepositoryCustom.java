package teamssavice.ssavice.serviceItem.infrastructure.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import teamssavice.ssavice.serviceItem.constants.ServiceStatusFilter;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;
import teamssavice.ssavice.serviceItem.service.dto.ServiceItemCommand;

public interface ServiceItemRepositoryCustom {

    Slice<ServiceItem> search(ServiceItemCommand.Search command);

    Page<ServiceItem> findByCompanyAndStatus(Long companyId, ServiceStatusFilter status, Pageable pageable);
}
