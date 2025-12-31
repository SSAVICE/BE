package teamssavice.ssavice.serviceItem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.company.entity.Company;
import teamssavice.ssavice.company.service.CompanyReadService;
import teamssavice.ssavice.global.dto.CursorResult;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;
import teamssavice.ssavice.serviceItem.service.dto.ServiceItemCommand;
import teamssavice.ssavice.serviceItem.service.dto.ServiceItemModel;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceItemService {
    private final CompanyReadService companyReadService;
    private final ServiceItemWriteService serviceItemWriteService;
    private final ServiceItemReadService serviceItemReadService;


    @Transactional
    public Long register(ServiceItemCommand.Create command) {

        Company company = companyReadService.findById(command.companyId());
        ServiceItem savedServiceItem = serviceItemWriteService.save(command, company);

        return savedServiceItem.getId();
    }

    @Transactional(readOnly = true)
    public CursorResult<ServiceItemModel.ItemInfo> search(ServiceItemCommand.Search command) {

        Slice<ServiceItem> items = serviceItemReadService.search(command);

        List<ServiceItemModel.ItemInfo> content = items.getContent().stream()
                .map(ServiceItemModel.ItemInfo::from)
                .toList();

        Long nextCursor = null;
        if (!content.isEmpty()) {
            nextCursor = content.getLast().id();
        }

        return new CursorResult<>(content, nextCursor, items.hasNext());
    }
}
