package teamssavice.ssavice.book.service;


import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.book.entity.Book;
import teamssavice.ssavice.book.entity.BookStatus;
import teamssavice.ssavice.book.service.dto.BookCommand;
import teamssavice.ssavice.book.service.dto.BookModel;
import teamssavice.ssavice.global.constants.ErrorCode;
import teamssavice.ssavice.global.exception.ConflictException;
import teamssavice.ssavice.global.exception.ForbiddenException;
import teamssavice.ssavice.imageresource.constants.ImageConstants;
import teamssavice.ssavice.refund.constants.RefundReason;
import teamssavice.ssavice.refund.service.RefundService;
import teamssavice.ssavice.s3.S3Service;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;
import teamssavice.ssavice.serviceItem.service.ServiceItemReadService;
import teamssavice.ssavice.serviceItem.service.dto.ServiceItemCommand;
import teamssavice.ssavice.user.entity.Users;
import teamssavice.ssavice.user.service.UserReadService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookReadService bookReadService;
    private final BookWriteService bookWriteService;
    private final ServiceItemReadService serviceItemReadService;
    private final UserReadService userReadService;
    private final RefundService refundService;
    private final S3Service s3Service;

    @Transactional(readOnly = true)
    public Page<BookModel.Info> getMyBooksByStatus(BookCommand.RetrieveByStatus command) {

        Page<Book> books = bookReadService.findAllByUserIdAndStatus(command.id(), command.status(), command.pageable());
        return books.map(BookModel.Info::from);
    }

    @Transactional(readOnly = true)
    public BookModel.Count getBookSummary(Long userId) {
        Long applying = bookReadService.countRecruitingBooksByUserId(userId);
        Long completedCount = bookReadService.countSucceededBooksByUserId(userId);
        Long totalCount = bookReadService.countAllBooksByUserId(userId);

        return BookModel.Count.from(applying, completedCount, totalCount);
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

    @Transactional
    public void cancel(ServiceItemCommand.Cancel command) {

        ServiceItem serviceItem = serviceItemReadService.findById(command.serviceId());
        Users user = userReadService.findById(command.userId());

        Book book = bookReadService.findFirstByUserIdAndServiceItemIdOrderByCreatedAtDesc(user.getId(), serviceItem.getId());

        // 최소 인원 검증인데 이거는 현재는 못하게 막아놓고 법적인거 조사하면서 따로 수수료 물면서 환불하는 로직으로 전환예정
        if (serviceItem.isReachedMinimum()) {
            throw new ConflictException(ErrorCode.AT_MINIMUM_MEMBER_LIMIT);
        }

        bookWriteService.cancel(book);

        serviceItem.cancelParticipation();
        refundService.registerRefunds(List.of(book), serviceItem.getPrice(), RefundReason.USER_CANCEL);
    }

    private void validateApply(Users user, ServiceItem serviceItem) {

        serviceItem.validateAppliable();

        if (bookReadService.existsByUserAndServiceAndStatusNot(user.getId(), serviceItem.getId(), BookStatus.CANCELED)) {
            throw new ConflictException(ErrorCode.ALREADY_APPLIED);
        }
    }

    @Transactional(readOnly = true)
    public Page<BookModel.Participant> getParticipants(Long companyId, Long serviceItemId,
        Pageable pageable) {
        ServiceItem serviceItem = serviceItemReadService.findById(serviceItemId);
        if (!serviceItem.isOwnedBy(companyId)) {
            throw new ForbiddenException(ErrorCode.NOT_SERVICE_OWNER);
        }
        Page<Book> books = bookReadService.findAllParticipantsByServiceItemId(serviceItemId,
            pageable);

        // S3 presigned URL 생성
        return books.map(book -> {
            String thumbnailUrl = book.getUser().hasImageResource()
                ? s3Service.generateGetPresignedUrl(
                book.getUser().getImageResource().getObjectKey())
                : s3Service.generateGetPresignedUrl(
                    ImageConstants.DEFAULT_PROFILE_IMAGE_OBJECT_KEY);
            return BookModel.Participant.of(book, thumbnailUrl);
        });
    }
}


