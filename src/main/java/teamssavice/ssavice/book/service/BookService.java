package teamssavice.ssavice.book.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.book.constants.BookStatus;
import teamssavice.ssavice.book.entity.Book;
import teamssavice.ssavice.book.service.dto.BookCommand;
import teamssavice.ssavice.book.service.dto.BookModel;
import teamssavice.ssavice.serviceItem.constants.ServiceStatus;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookReadService bookReadService;

    @Transactional(readOnly = true)
    public Page<BookModel.Info> getMyBooksByStatus(BookCommand.RetrieveByStatus command) {
        Page<Book> books = bookReadService.findAllByUserIdAndStatus(
            command.userId(),
            command.status(),
            command.pageable()
        );

        return books.map(BookModel.Info::from);
    }

    @Transactional(readOnly = true)
    public BookModel.BookSummary getBookSummary(Long userId) {

        Long applying = bookReadService.countByUserIdAndBookStatusAndServiceStatus(
                userId, BookStatus.RESERVED, ServiceStatus.RECRUITING
        );

        // 모집 성공
        Long succeededCount = bookReadService.countByUserIdAndBookStatusAndServiceStatus(
                userId, BookStatus.RESERVED, ServiceStatus.SUCCEEDED
        );
        // 모집 마감
        Long closedCount = bookReadService.countByUserIdAndBookStatusAndServiceStatus(
                userId, BookStatus.RESERVED, ServiceStatus.FULLED
        );

        return BookModel.BookSummary.from(applying, succeededCount + closedCount);
    }
}


