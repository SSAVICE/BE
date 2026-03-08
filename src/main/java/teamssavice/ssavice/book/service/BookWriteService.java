package teamssavice.ssavice.book.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.book.entity.BookStatus;
import teamssavice.ssavice.book.entity.Book;
import teamssavice.ssavice.book.infrastructure.repository.BookRepository;
import teamssavice.ssavice.global.constants.ErrorCode;
import teamssavice.ssavice.global.exception.ConflictException;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;
import teamssavice.ssavice.user.entity.Users;

@Service
@RequiredArgsConstructor
@Transactional
public class BookWriteService {

    private final BookRepository bookRepository;

    @Transactional
    public Book apply(Users user, ServiceItem serviceItem) {
        Book book = Book.builder()
                .user(user)
                .serviceItem(serviceItem)
                .bookStatus(BookStatus.RESERVED)
                .build();
        return bookRepository.save(book);
    }

    @Transactional
    public void cancel(Book book) {
        book.cancel();
    }
}
