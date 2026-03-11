package teamssavice.ssavice.serviceItem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.address.AddressCommand;
import teamssavice.ssavice.book.entity.Book;
import teamssavice.ssavice.book.entity.BookStatus;
import teamssavice.ssavice.book.service.BookReadService;
import teamssavice.ssavice.company.entity.Company;
import teamssavice.ssavice.company.service.CompanyReadService;
import teamssavice.ssavice.global.constants.ErrorCode;
import teamssavice.ssavice.global.dto.CursorResult;
import teamssavice.ssavice.global.dto.SearchCursorResult;
import teamssavice.ssavice.global.exception.ForbiddenException;
import teamssavice.ssavice.global.util.GeoHashUtil;
import teamssavice.ssavice.imageresource.entity.ImageResource;
import teamssavice.ssavice.imageresource.service.ImageReadService;
import teamssavice.ssavice.kafka.event.KafkaEvent;
import teamssavice.ssavice.outbox.constants.EventType;
import teamssavice.ssavice.outbox.service.OutboxWriteService;
import teamssavice.ssavice.refund.constants.RefundReason;
import teamssavice.ssavice.refund.service.RefundService;
import teamssavice.ssavice.region.Region;
import teamssavice.ssavice.region.RegionReadService;
import teamssavice.ssavice.s3.S3Service;
import teamssavice.ssavice.s3.event.S3EventDto;
import teamssavice.ssavice.serviceItem.constants.ServiceStatus;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;
import teamssavice.ssavice.serviceItem.infrastructure.opensearch.SearchResult;
import teamssavice.ssavice.serviceItem.infrastructure.opensearch.ServiceItemSearchDocument;
import teamssavice.ssavice.serviceItem.service.dto.ServiceItemCommand;
import teamssavice.ssavice.serviceItem.service.dto.ServiceItemModel;
import teamssavice.ssavice.wish.service.WishReadService;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ServiceItemService {
    private final ApplicationEventPublisher applicationEventPublisher;
    private final CompanyReadService companyReadService;
    private final ServiceItemWriteService serviceItemWriteService;
    private final ServiceItemReadService serviceItemReadService;
    private final ImageReadService imageReadService;
    private final S3Service s3Service;
    private final BookReadService bookReadService;
    private final RegionReadService regionReadService;
    private final RefundService refundService;
    private final WishReadService wishReadService;
    private final OutboxWriteService outboxWriteService;

    @Transactional
    public Long register(ServiceItemCommand.Create command) {
        Company company = companyReadService.findById(command.companyId());
        List<ImageResource> imageResourceList = imageReadService.findAllBySourceKeyIn(
            command.imageObjectKeys());
        Region region = regionReadService.findByRegionCode(command.regionCode());
        ServiceItem savedServiceItem = serviceItemWriteService.save(command, company,
            AddressCommand.RegionInfo.from(command, region));

        if (!imageResourceList.isEmpty()) {
            ImageResource thumbnailImage = imageResourceList.getFirst();
            thumbnailImage.checkedConfirmed();
            thumbnailImage.changeTargetKeyAsThumbnail();
            savedServiceItem.updateThumbNailImage(thumbnailImage);
            applicationEventPublisher.publishEvent(S3EventDto.Move.from(thumbnailImage));
            imageResourceList.removeFirst();
        }

        for (ImageResource imageResource : imageResourceList) {
            imageResource.checkedConfirmed();
            imageResource.activate();
            savedServiceItem.addImageId(imageResource.getId());
            applicationEventPublisher.publishEvent(S3EventDto.Move.from(imageResource));
        }

        applicationEventPublisher.publishEvent(KafkaEvent.Join.createEvent(savedServiceItem, command.companyId()));

        outboxWriteService.saveEvent(
                savedServiceItem.getId(),
                EventType.CREATED,
                ServiceItemSearchDocument.from(savedServiceItem)
        );

        return savedServiceItem.getId();
    }

    @Transactional(readOnly = true)
    public CursorResult<ServiceItemModel.Search> search(@Nullable Long userId, ServiceItemCommand.Search command) {

        Slice<ServiceItem> items = serviceItemReadService.search(command);
        Set<Long> set = (userId != null)
                ? bookReadService.findReservedServiceItemIdsFromLatestBooks(userId, items.getContent())
                : Collections.emptySet();

        List<ServiceItemModel.Search> content = items.getContent().stream()
            .map(entity -> {
                double distanceKm = GeoHashUtil.calculateDistanceInKm(
                    command.userLatitude(), command.userLongitude(),
                    entity.getAddress().getLatitude(), entity.getAddress().getLongitude());
                return ServiceItemModel.Search.from(entity, set.contains(entity.getId()),
                    s3Service.generateGetPresignedUrl(entity.getObjectKey()), distanceKm);
            })
            .toList();

        Long nextCursor = null;
        if (!content.isEmpty()) {
            nextCursor = content.getLast().serviceId();
        }

        return new CursorResult<>(content, nextCursor, items.hasNext());
    }

    public SearchCursorResult<ServiceItemModel.Search> searchV2(@Nullable Long userId, ServiceItemCommand.Search command) {

        SearchResult result = serviceItemReadService.searchByOpenSearch(command);

        // 예약 여부 조회
        List<Long> serviceItemIds = result.items().stream()
                .map(item -> item.document().getId())
                .toList();

        Set<Long> reservedIds = (userId != null)
                ? bookReadService.findReservedServiceItemIdsByIds(userId, serviceItemIds)
                : Collections.emptySet();

        Map<Long, ServiceItem> serviceItemMap = serviceItemReadService.findAllByServiceItemIds(serviceItemIds);

        List<ServiceItemModel.Search> content = result.items().stream()
                .map(item -> {
                    ServiceItemSearchDocument doc = item.document();

                    double distanceKm;
                    if (item.distanceKm() != null) {
                        // DISTANCE 정렬은 OpenSearch 계산값
                        distanceKm = item.distanceKm();
                    } else {
                        // 나머지 정렬에 대해서는 GeoHashUtil (JAVA) 를 통해 계산
                        distanceKm = GeoHashUtil.calculateDistanceInKm(
                                command.userLatitude(), command.userLongitude(),
                                doc.getLocation().getLat(),
                                doc.getLocation().getLon());
                    }

                    return ServiceItemModel.Search.fromDocument(
                            doc,
                            new ServiceItemModel.SearchContext(
                                    reservedIds.contains(doc.getId()),
                                    s3Service.generateGetPresignedUrl(doc.getThumbnailObjectKey()),
                                    distanceKm,
                                    serviceItemMap.get(doc.getId()).getCurrentMember(),
                                    serviceItemMap.get(doc.getId()).getStatus()
                            )
                    );

                })
                .toList();

        return new SearchCursorResult<>(content, result.nextSearchAfter(), result.hasNext());
    }

    @Transactional(readOnly = true)
    public ServiceItemModel.Detail getServiceDetail(Long serviceId, @Nullable Long userId) {
        ServiceItem serviceItem = serviceItemReadService.findByIdWithAddressAndImageList(serviceId);
        List<ImageResource> imageList = imageReadService.findAllById(serviceItem.getImageIds());
        List<String> imageUrls = new ArrayList<>();
        for (ImageResource imageResource : imageList) {
            imageUrls.add(s3Service.generateGetPresignedUrl(imageResource.getResolveKey()));
        }

        boolean isLiked = (userId != null) && wishReadService.existsByUserIdAndServiceItemId(userId, serviceId);
        boolean isBooked = (userId != null) && bookReadService.isBookedByUserIdAndServiceId(userId, serviceId);

        return ServiceItemModel.Detail.from(serviceItem, imageUrls, isLiked, isBooked);
    }

    @Transactional(readOnly = true)
    public ServiceItemModel.Summary getServiceItemSummary(Long serviceId) {
        ServiceItem serviceItem = serviceItemReadService.findByIdWithAddressAndThumbnail(serviceId);
        return ServiceItemModel.Summary.from(serviceItem,
            s3Service.generateGetPresignedUrl(serviceItem.getObjectKey()));
    }

    @Transactional(readOnly = true)
    public Page<ServiceItemModel.Summary> getServiceItemByCompanyAndStatus(ServiceItemCommand.RetrieveByCompanyAndStatus command) {
        Page<ServiceItem> serviceItems = serviceItemReadService.findByCompanyAndStatus(command);
        return serviceItems.map(serviceItem -> ServiceItemModel.Summary.from(serviceItem,
            s3Service.generateGetPresignedUrl(serviceItem.getObjectKey())));
    }

    @Transactional(readOnly = true)
    public Page<ServiceItemModel.Summary> getServiceItemByCompanyAndOnSale(ServiceItemCommand.RetrieveByCompanyAndOnSale command) {
        if (command.onSale()) {
            Page<ServiceItem> serviceItems = serviceItemReadService.findAllByCompany_IdAndStatus(command.companyId(), ServiceStatus.RECRUITING, command.pageable());
            return serviceItems.map(serviceItem -> ServiceItemModel.Summary.from(serviceItem,
                s3Service.generateGetPresignedUrl(serviceItem.getObjectKey())));
        }
        Page<ServiceItem> serviceItems = serviceItemReadService.findAllByCompany_Id(command.companyId(), command.pageable());
        return serviceItems.map(
            serviceItem -> ServiceItemModel.Summary.from(serviceItem,
                s3Service.generateGetPresignedUrl(serviceItem.getObjectKey())));
    }

    @Transactional
    public void delete(ServiceItemCommand.Delete command) {

        ServiceItem serviceItem = serviceItemReadService.findById(command.serviceId());
        validateOwner(command.companyId(), serviceItem);

        serviceItem.delete();

        // 이거는 확장성을 고려해서 만들어둠 - 관련해서 이벤트 처리 방식으로 수정 예정
        List<Book> canceledBooks = bookReadService.findAllByServiceItemIdAndBookStatus(serviceItem.getId(), BookStatus.RESERVED);

        if (!canceledBooks.isEmpty()) {
            refundService.registerRefunds(canceledBooks, serviceItem.getPrice(), RefundReason.SERVICE_DELETED);
        }

        outboxWriteService.saveEvent(
                serviceItem.getId(),
                EventType.DELETED,
                Map.of("id", serviceItem.getId())
        );

    }

    @Transactional(readOnly = true)
    public ServiceItemModel.Count getCompanysServiceItemCount(Long companyId) {
        Long applying = serviceItemReadService.countRecruitingServiceItemsByCompanyId(companyId);
        Long completedCount = serviceItemReadService.countSucceededServiceItemsByCompanyId(companyId);
        Long totalCount = serviceItemReadService.countAllServiceItemsByCompanyId(companyId);

        return ServiceItemModel.Count.from(applying, completedCount, totalCount);
    }

    @Transactional(readOnly = true)
    public CursorResult<ServiceItemModel.Nearby> searchNearby(ServiceItemCommand.Nearby command) {
        Slice<ServiceItem> slice = serviceItemReadService.findNearbyByGeoHash(
            command.latitude(), command.longitude(),
            command.userLatitude(), command.userLongitude(),
            command.radiusMeters(), command.size(), command.lastId());

        List<ServiceItemModel.Nearby> content = slice.getContent().stream()
            .map(item -> {
                double distanceKm = GeoHashUtil.calculateDistanceInKm(
                    command.userLatitude(), command.userLongitude(),
                    item.getAddress().getLatitude(), item.getAddress().getLongitude());
                return ServiceItemModel.Nearby.from(item, distanceKm,
                    s3Service.generateGetPresignedUrl(item.getObjectKey()));
            })
            .toList();

        Long nextCursor = null;
        if (!content.isEmpty()) {
            nextCursor = content.getLast().serviceId();
        }

        return new CursorResult<>(content, nextCursor, slice.hasNext());
    }

    private void validateOwner(Long companyId, ServiceItem serviceItem) {
        if (!serviceItem.getCompany().getId().equals(companyId)) {
            throw new ForbiddenException(ErrorCode.NOT_SERVICE_OWNER);
        }
    }

}
