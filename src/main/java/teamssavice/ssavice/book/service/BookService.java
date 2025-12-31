package teamssavice.ssavice.book.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
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
}


