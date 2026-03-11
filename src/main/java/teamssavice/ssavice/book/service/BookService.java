package teamssavice.ssavice.book.service;


import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
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
import teamssavice.ssavice.kafka.event.KafkaEvent;
import teamssavice.ssavice.outbox.constants.EventType;
import teamssavice.ssavice.outbox.service.OutboxWriteService;
import teamssavice.ssavice.refund.constants.RefundReason;
import teamssavice.ssavice.refund.service.RefundService;
import teamssavice.ssavice.s3.S3Service;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;
import teamssavice.ssavice.serviceItem.service.ServiceItemReadService;
import teamssavice.ssavice.serviceItem.service.ServiceItemWriteService;
import teamssavice.ssavice.serviceItem.service.dto.ServiceItemCommand;
import teamssavice.ssavice.user.entity.Users;
import teamssavice.ssavice.user.service.UserReadService;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BookService {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final BookReadService bookReadService;
    private final BookWriteService bookWriteService;
    private final ServiceItemReadService serviceItemReadService;
    private final UserReadService userReadService;
    private final RefundService refundService;
    private final S3Service s3Service;
    private final ServiceItemWriteService serviceItemWriteService;
    private final OutboxWriteService outboxWriteService;

    @Transactional(readOnly = true)
    public Page<BookModel.Info> getMyBooksByStatus(BookCommand.RetrieveByStatus command) {

        Page<Book> books = bookReadService.findAllByUserIdAndStatus(command.id(), command.status(),
                command.pageable());
        return books.map(book -> {
            String presignedUrl = s3Service.generateGetPresignedUrl(
                    book.getServiceItem().getObjectKey());
            return BookModel.Info.from(book, presignedUrl);
        });
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

        Users user = userReadService.findById(userId);
        validateApply(user, serviceId);

        ServiceItem serviceItem = serviceItemWriteService.participate(serviceId);

        // 만약 인원이 가득 차면  opensearch 에 상태 변화에 대한 업데이트 발행
        if (serviceItem.isFull()) {
            outboxWriteService.saveEvent(
                    serviceItem.getId(),
                    EventType.AVAILABILITY_UPDATED,
                    Map.of("isAvailable", false)
            );
        }

        Book book = bookWriteService.apply(user, serviceItem);
        applicationEventPublisher.publishEvent(KafkaEvent.Join.joinEvent(serviceItem, userId));

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
        applicationEventPublisher.publishEvent(KafkaEvent.Join.leaveEvent(serviceItem, command.userId()));
        refundService.registerRefunds(List.of(book), serviceItem.getPrice(), RefundReason.USER_CANCEL);
    }

    private void validateApply(Users user, Long serviceItemId) {
        if (bookReadService.existsByUserAndServiceAndStatusNot(user.getId(), serviceItemId, BookStatus.CANCELED)) {
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
            String thumbnailUrl = s3Service.generateGetPresignedUrl(book.getUser().getObjectKey());
            return BookModel.Participant.of(book, thumbnailUrl);
        });
    }
}


