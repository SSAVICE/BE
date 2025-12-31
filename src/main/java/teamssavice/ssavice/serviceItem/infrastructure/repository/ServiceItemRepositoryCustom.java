package teamssavice.ssavice.serviceItem.infrastructure.repository;

import org.springframework.data.domain.Slice;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;
import teamssavice.ssavice.serviceItem.service.dto.ServiceItemCommand;

public interface ServiceItemRepositoryCustom {

    Slice<ServiceItem> search(ServiceItemCommand.Search command);
}
