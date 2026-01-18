package teamssavice.ssavice.serviceItem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.address.AddressCommand;
import teamssavice.ssavice.book.entity.Book;
import teamssavice.ssavice.book.entity.BookStatus;
import teamssavice.ssavice.book.service.BookReadService;
import teamssavice.ssavice.book.service.BookWriteService;
import teamssavice.ssavice.book.service.dto.BookModel;
import teamssavice.ssavice.company.entity.Company;
import teamssavice.ssavice.company.service.CompanyReadService;
import teamssavice.ssavice.global.constants.ErrorCode;
import teamssavice.ssavice.global.dto.CursorResult;
import teamssavice.ssavice.global.exception.ConflictException;
import teamssavice.ssavice.global.exception.ForbiddenException;
import teamssavice.ssavice.imageresource.entity.ImageResource;
import teamssavice.ssavice.imageresource.service.ImageReadService;
import teamssavice.ssavice.refund.constants.RefundReason;
import teamssavice.ssavice.refund.service.RefundService;
import teamssavice.ssavice.region.Region;
import teamssavice.ssavice.region.RegionReadService;
import teamssavice.ssavice.s3.S3Service;
import teamssavice.ssavice.s3.event.S3EventDto;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;
import teamssavice.ssavice.serviceItem.service.dto.ServiceItemCommand;
import teamssavice.ssavice.serviceItem.service.dto.ServiceItemModel;
import teamssavice.ssavice.user.entity.Users;
import teamssavice.ssavice.user.service.UserReadService;

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
    private final RefundService refundService;

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

        serviceItem.participate();

        Book book = bookWriteService.apply(user, serviceItem);

        return BookModel.Apply.of(book.getId());
    }

    private void validateApply(Users user, ServiceItem serviceItem) {

        serviceItem.validateAppliable();

        if (bookReadService.existsByUserAndServiceAndStatusNot(user.getId(), serviceItem.getId(), BookStatus.CANCELED)) {
            throw new ConflictException(ErrorCode.ALREADY_APPLIED);
        }
    }

    public Page<ServiceItemModel.Summary> getServiceByCompanyAndStatus(ServiceItemCommand.RetrieveByCompanyAndOnSale command) {
        if (command.onSale()) {
            Page<ServiceItem> serviceItems = serviceItemReadService.findAllRecruitingByCompany_Id(command.companyId(), command.pageable());
            return serviceItems.map(ServiceItemModel.Summary::from);
        }
        Page<ServiceItem> serviceItems = serviceItemReadService.findAllByCompany_Id(command.companyId(), command.pageable());
        return serviceItems.map(ServiceItemModel.Summary::from);
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
    }

    private void validateOwner(Long companyId, ServiceItem serviceItem) {
        if (!serviceItem.getCompany().getId().equals(companyId)) {
            throw new ForbiddenException(ErrorCode.NOT_SERVICE_OWNER);
        }
    }

    @Transactional
    public void cancel(ServiceItemCommand.Cancel command) {

        ServiceItem serviceItem = serviceItemReadService.findById(command.serviceId());
        Users user = userReadService.findById(command.userId());

        Book book = bookReadService.findFirstByUserIdAndServiceItemIdOrderByCreatedAtDesc(user.getId(), serviceItem.getId());

        if (book.isCanceled()) {
            throw new ConflictException(ErrorCode.ALREADY_CANCELED);
        }

        // 최소 인원 검증인데 이거는 현재는 못하게 막아놓고 법적인거 조사하면서 따로 수수료 물면서 환불하는 로직으로 전환예정
        if (serviceItem.isReachedMinimum()) {
            throw new ConflictException(ErrorCode.AT_MINIMUM_MEMBER_LIMIT);
        }

        book.cancel();
        serviceItem.cancelParticipation();
        refundService.registerRefunds(List.of(book), serviceItem.getPrice(), RefundReason.USER_CANCEL);
    }
}
