package teamssavice.ssavice.serviceItem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.global.constants.ErrorCode;
import teamssavice.ssavice.global.exception.EntityNotFoundException;
import teamssavice.ssavice.global.util.GeoHashUtil;
import teamssavice.ssavice.serviceItem.constants.ServiceStatus;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;
import teamssavice.ssavice.serviceItem.infrastructure.opensearch.SearchResult;
import teamssavice.ssavice.serviceItem.infrastructure.opensearch.ServiceItemSearchClient;
import teamssavice.ssavice.serviceItem.infrastructure.repository.ServiceItemRepository;
import teamssavice.ssavice.serviceItem.service.dto.ServiceItemCommand;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ServiceItemReadService {

    private final ServiceItemRepository serviceItemRepository;
    private final ServiceItemSearchClient serviceItemSearchClient;

    @Transactional(readOnly = true)
    public Slice<ServiceItem> search(ServiceItemCommand.Search command) {
        return serviceItemRepository.search(command);
    }

    @Transactional(readOnly = true)
    public List<ServiceItem> findTop5ByCompanyIdOrderByDeadlineDesc(Long companyId) {
        return serviceItemRepository.findTop5ByCompanyIdOrderByDeadlineDesc(companyId, PageRequest.of(0, 5));
    }

    @Transactional(readOnly = true)
    public ServiceItem findById(Long serviceId) {
        return serviceItemRepository.findById(serviceId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SERVICE_ITEM_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Page<ServiceItem> findByCompanyAndStatus(ServiceItemCommand.RetrieveByCompanyAndStatus command) {
        return serviceItemRepository.findByCompanyAndStatus(command.companyId(), command.status(), command.pageable());
    }

    @Transactional(readOnly = true)
    public Page<ServiceItem> findAllByCompany_Id(Long companyId, Pageable pageable) {
        return serviceItemRepository.findAllByCompany_Id(companyId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<ServiceItem> findAllByCompany_IdAndStatus(Long companyId, ServiceStatus status, Pageable pageable) {
        return serviceItemRepository.findAllByCompany_IdAndStatus(companyId, status, LocalDateTime.now(), pageable);
    }

    @Transactional(readOnly = true)
    public ServiceItem getReferenceById(Long serviceId) {
        return serviceItemRepository.getReferenceById(serviceId);
    }

    @Transactional(readOnly = true)
    public ServiceItem findByIdWithAddressAndImageList(Long id) {
        return serviceItemRepository.findByIdWithAddressAndImageList(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.SERVICE_ITEM_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Long countRecruitingServiceItemsByCompanyId(Long companyId) {
        return serviceItemRepository.countRecruitingServiceItemsByCompanyId(companyId, ServiceStatus.RECRUITING, LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public Long countSucceededServiceItemsByCompanyId(Long companyId) {
        return serviceItemRepository.countSucceededServiceItemsByCompanyId(companyId, ServiceStatus.SUCCEEDED, LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public Long countAllServiceItemsByCompanyId(Long companyId) {
        return serviceItemRepository.countAllByCompany_Id(companyId);
    }

    @Transactional(readOnly = true)
    public Slice<ServiceItem> findNearbyByGeoHash(
            BigDecimal latitude, BigDecimal longitude,
            BigDecimal userLatitude, BigDecimal userLongitude,
            int radiusMeters, int size, Long lastId) {
        int queryPrecision = GeoHashUtil.getPrecisionForRadius(radiusMeters);
        String centerHash = GeoHashUtil.encode(latitude, longitude, queryPrecision);
        List<String> neighbors = GeoHashUtil.getNeighbors(centerHash);

        return serviceItemRepository.findNearbyByGeoHashes(
            latitude, longitude, userLatitude, userLongitude,
            radiusMeters, neighbors, size, lastId);
    }

    @Transactional(readOnly = true)
    public SearchResult searchByOpenSearch(ServiceItemCommand.Search command) {
        return serviceItemSearchClient.search(command);
    }
}
