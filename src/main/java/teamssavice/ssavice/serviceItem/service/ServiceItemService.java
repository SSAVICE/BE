package teamssavice.ssavice.serviceItem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.company.entity.Company;
import teamssavice.ssavice.company.service.CompanyReadService;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;
import teamssavice.ssavice.serviceItem.service.dto.ServiceItemCommand;

@Service
@RequiredArgsConstructor
public class ServiceItemService {
    private final CompanyReadService companyReadService;
    private final ServiceItemWriteService serviceItemWriteService;


    @Transactional
    public Long register(ServiceItemCommand.Create command) {

        Company company = companyReadService.findByUserId(command.userId());
        ServiceItem savedServiceItem = serviceItemWriteService.save(command, company);

        return savedServiceItem.getId();
    }
}
