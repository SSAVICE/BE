package teamssavice.ssavice.serviceItem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.address.AddressCommand;
import teamssavice.ssavice.book.constants.BookStatus;
import teamssavice.ssavice.book.entity.Book;
import teamssavice.ssavice.book.service.BookReadService;
import teamssavice.ssavice.book.service.BookWriteService;
import teamssavice.ssavice.book.service.dto.BookModel;
import teamssavice.ssavice.company.entity.Company;
import teamssavice.ssavice.company.service.CompanyReadService;
import teamssavice.ssavice.global.constants.ErrorCode;
import teamssavice.ssavice.global.dto.CursorResult;
import teamssavice.ssavice.global.exception.ConflictException;
import teamssavice.ssavice.imageresource.entity.ImageResource;
import teamssavice.ssavice.imageresource.service.ImageReadService;
import teamssavice.ssavice.region.Region;
import teamssavice.ssavice.region.RegionReadService;
import teamssavice.ssavice.s3.S3Service;
import teamssavice.ssavice.s3.event.S3EventDto;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;
import teamssavice.ssavice.serviceItem.service.dto.ServiceItemCommand;
import teamssavice.ssavice.serviceItem.service.dto.ServiceItemModel;
import teamssavice.ssavice.user.entity.Users;
import teamssavice.ssavice.user.service.UserReadService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceItemService {
    private final ApplicationEventPublisher applicationEventPublisher;
    private final CompanyReadService companyReadService;
    private final ServiceItemWriteService serviceItemWriteService;
    private final ServiceItemReadService serviceItemReadService;
    private final ImageReadService imageReadService;
    private final S3Service s3Service;
    private final UserReadService userReadService;
    private final BookWriteService bookWriteService;
    private final BookReadService bookReadService;
    private final RegionReadService regionReadService;

    @Transactional
    public Long register(ServiceItemCommand.Create command) {
        Company company = companyReadService.findById(command.companyId());
        List<ImageResource> imageResourceList = imageReadService.findAllByTempKeyIn(command.imageObjectKeys());
        Region region = regionReadService.findByRegionCode(command.regionCode());
        ServiceItem savedServiceItem = serviceItemWriteService.save(command, company, AddressCommand.RegionInfo.from(command, region));

        for (ImageResource imageResource : imageResourceList) {
            imageResource.activate();
            savedServiceItem.addImageId(imageResource.getId());
            applicationEventPublisher.publishEvent(S3EventDto.Move.from(imageResource));
        }

        return savedServiceItem.getId();
    }

    @Transactional(readOnly = true)
    public CursorResult<ServiceItemModel.Search> search(ServiceItemCommand.Search command) {

        Slice<ServiceItem> items = serviceItemReadService.search(command);

        List<ServiceItemModel.Search> content = items.getContent().stream()
                .map(ServiceItemModel.Search::from)
                .toList();

        Long nextCursor = null;
        if (!content.isEmpty()) {
            nextCursor = content.getLast().serviceId();
        }

        return new CursorResult<>(content, nextCursor, items.hasNext());
    }

    @Transactional(readOnly = true)
    public ServiceItemModel.Detail getServiceDetail(Long serviceId) {
        ServiceItem serviceItem = serviceItemReadService.findById(serviceId);
        List<ImageResource> imageList = imageReadService.findAllById(serviceItem.getImageIds());
        List<String> imageUrls = new ArrayList<>();
        for (ImageResource imageResource : imageList) {
            imageUrls.add(s3Service.generateGetPresignedUrl(imageResource.getObjectKey()));
        }
        return ServiceItemModel.Detail.from(serviceItem, imageUrls);
    }

    @Transactional
    public BookModel.Apply apply(Long userId, Long serviceId) {

        ServiceItem serviceItem = serviceItemReadService.findById(serviceId);
        Users user = userReadService.findById(userId);

        validateApply(user, serviceItem);

        boolean alreadyReachedMin = serviceItem.isReachedMinimum();

        serviceItem.participate();

        // 최소인원 도달시 기존 APPLYING 상태 참여자들 일괄 MATCHED 변경
        if (!alreadyReachedMin && serviceItem.isReachedMinimum()) {
            bookWriteService.updateAllStatusToMatched(serviceItem.getId());
        }

        BookStatus initialStatus = serviceItem.isReachedMinimum()
                ? BookStatus.MATCHED
                : BookStatus.APPLYING;

        Book book = bookWriteService.save(user, serviceItem, initialStatus);

        return BookModel.Apply.from(book);
    }

    private void validateApply(Users user, ServiceItem serviceItem) {

        serviceItem.validateAppliable(LocalDateTime.now());

        if (bookReadService.existsByUserAndServiceItem(user, serviceItem)) {
            throw new ConflictException(ErrorCode.ALREADY_APPLIED);
        }
    }


}
