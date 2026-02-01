package teamssavice.ssavice.book.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.book.entity.Book;
import teamssavice.ssavice.book.service.dto.BookCommand;
import teamssavice.ssavice.book.service.dto.BookModel;
import teamssavice.ssavice.imageresource.constants.ImageConstants;
import teamssavice.ssavice.s3.S3Service;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookReadService bookReadService;
    private final S3Service s3Service;

    @Transactional(readOnly = true)
    public Page<BookModel.Info> getMyBooksByStatus(BookCommand.RetrieveByStatus command) {

        Page<Book> books = bookReadService.findAllByUserIdAndStatus(command.id(), command.status(),
            command.pageable());
        return books.map(book -> {
            String presignedUrl = toPresignedUrl(book,
                ImageConstants.DEFAULT_SERVICE_ITEM_IMAGE_OBJECT_KEY);
            return BookModel.Info.from(book, presignedUrl);
        });
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

        return books.map(book -> {
            String presignedUrl = toPresignedUrl(book,
                ImageConstants.DEFAULT_SERVICE_ITEM_IMAGE_OBJECT_KEY);
            return BookModel.Info.from(book, presignedUrl);
        });
    }

    @Transactional(readOnly = true)
    public BookModel.BookSummary getCompanysBookSummary(Long companyId) {
        Long applying = bookReadService.countRecruitingBooksByCompanyId(companyId);
        Long completedCount = bookReadService.countSucceededBooksByCompanyId(companyId);

        return BookModel.BookSummary.from(applying, completedCount);
    }

    private String toPresignedUrl(Book book, String defaultObjectKey) {
        String objectKey = ImageConstants.DEFAULT_SERVICE_ITEM_IMAGE_OBJECT_KEY;
        ServiceItem serviceItem = book.getServiceItem();
        if (serviceItem.hasThumbnailImage()) {
            objectKey = serviceItem.getThumbnailImageResource().getObjectKey();
        }
        return s3Service.generateGetPresignedUrl(objectKey);
    }
}


