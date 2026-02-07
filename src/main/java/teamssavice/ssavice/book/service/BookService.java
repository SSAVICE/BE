package teamssavice.ssavice.book.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.book.entity.Book;
import teamssavice.ssavice.book.service.dto.BookCommand;
import teamssavice.ssavice.book.service.dto.BookModel;
import teamssavice.ssavice.global.constants.ErrorCode;
import teamssavice.ssavice.global.exception.ForbiddenException;
import teamssavice.ssavice.imageresource.constants.ImageConstants;
import teamssavice.ssavice.s3.S3Service;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;
import teamssavice.ssavice.serviceItem.service.ServiceItemReadService;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookReadService bookReadService;
    private final ServiceItemReadService serviceItemReadService;
    private final S3Service s3Service;

    @Transactional(readOnly = true)
    public Page<BookModel.Info> getMyBooksByStatus(BookCommand.RetrieveByStatus command) {

        Page<Book> books = bookReadService.findAllByUserIdAndStatus(command.id(), command.status(),
            command.pageable());
        return books.map(BookModel.Info::from);
    }

    @Transactional(readOnly = true)
    public BookModel.BookSummary getBookSummary(Long userId) {
        Long applying = bookReadService.countRecruitingBooksByUserId(userId);
        Long completedCount = bookReadService.countSucceededBooksByUserId(userId);

        return BookModel.BookSummary.from(applying, completedCount);
    }

    @Transactional(readOnly = true)
    public Page<BookModel.Info> getMyCompanysBooksByStatus(BookCommand.RetrieveByStatus command) {
        Page<Book> books = bookReadService.findAllByCompanyIdAndStatus(command.id(),
            command.status(), command.pageable());
        return books.map(BookModel.Info::from);
    }

    @Transactional(readOnly = true)
    public BookModel.BookSummary getCompanysBookSummary(Long companyId) {
        Long applying = bookReadService.countRecruitingBooksByCompanyId(companyId);
        Long completedCount = bookReadService.countSucceededBooksByCompanyId(companyId);

        return BookModel.BookSummary.from(applying, completedCount);
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


