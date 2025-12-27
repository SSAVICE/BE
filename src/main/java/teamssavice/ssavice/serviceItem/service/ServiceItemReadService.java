package teamssavice.ssavice.serviceItem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.company.entity.Company;
import teamssavice.ssavice.global.constants.ErrorCode;
import teamssavice.ssavice.global.exception.EntityNotFoundException;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;
import teamssavice.ssavice.serviceItem.infrastructure.repository.ServiceItemRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceItemReadService {
    private final ServiceItemRepository serviceItemRepository;

    @Transactional(readOnly = true)
    public List<ServiceItem> findTop5ByCompanyOrderByDeadlineDesc(Company company) {
        return serviceItemRepository.findTop5ByCompanyOrderByDeadlineDesc(company);
    }

    @Transactional(readOnly = true)
    public ServiceItem findById(Long id) {
        return serviceItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SERVICE_NOT_FOUND));
    }
}
