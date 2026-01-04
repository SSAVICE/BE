package teamssavice.ssavice.book.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.book.constants.BookStatus;
import teamssavice.ssavice.book.entity.Book;
import teamssavice.ssavice.book.service.dto.BookCommand;
import teamssavice.ssavice.book.service.dto.BookModel;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookReadService bookReadService;


    public Page<BookModel.Info> getMyBooks(BookCommand.Retrieve command) {
        Page<Book> books = bookReadService.findAllByUserId(command.userId(), command.pageable());

        return books.map(BookModel.Info::from);
    }

    @Transactional(readOnly = true)
    public BookModel.BookSummary getBookSummary(Long userId) {
        Long applying = bookReadService.countByUserIdAndBookStatus(userId, BookStatus.APPLYING);
        Long completed = bookReadService.countByUserIdAndBookStatus(userId, BookStatus.COMPLETED);

        return BookModel.BookSummary.from(applying, completed);
    }
}


