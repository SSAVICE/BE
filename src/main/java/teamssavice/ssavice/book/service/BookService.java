package teamssavice.ssavice.book.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.book.entity.BookStatus;
import teamssavice.ssavice.book.entity.Book;
import teamssavice.ssavice.book.service.dto.BookCommand;
import teamssavice.ssavice.book.service.dto.BookModel;
import teamssavice.ssavice.serviceItem.constants.ServiceStatus;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookReadService bookReadService;

    @Transactional(readOnly = true)
    public Page<BookModel.Info> getMyBooksByStatus(BookCommand.RetrieveByStatus command) {

        Page<Book> books = bookReadService.findAllByUserIdAndStatus(command.userId(), command.status(), command.pageable());
        return books.map(BookModel.Info::from);
    }

    @Transactional(readOnly = true)
    public BookModel.BookSummary getBookSummary(Long userId) {
        List<Book> books = bookReadService.findByUserIdAndBookStatus(userId, BookStatus.RESERVED);
        Long applying = 0L;
        Long completedCount = 0L;
        for (Book book : books) {
            if(book.getServiceItem().getStatus() == ServiceStatus.RECRUITING) applying++;
            else if(book.getServiceItem().getStatus() == ServiceStatus.SUCCEEDED ||
                    book.getServiceItem().getStatus() == ServiceStatus.FULLED) completedCount++;
        }

        return BookModel.BookSummary.from(applying, completedCount);
    }
}


