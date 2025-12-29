package teamssavice.ssavice.serviceItem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;
import teamssavice.ssavice.serviceItem.infrastructure.repository.ServiceItemRepository;
import teamssavice.ssavice.serviceItem.service.dto.ServiceItemCommand;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ServiceItemReadService {

    private final ServiceItemRepository serviceItemRepository;

    public Slice<ServiceItem> search(ServiceItemCommand.Search command) {
        return serviceItemRepository.search(command);
    }
}
