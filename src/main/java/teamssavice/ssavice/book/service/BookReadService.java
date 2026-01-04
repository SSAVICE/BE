package teamssavice.ssavice.book.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.book.constants.BookStatus;
import teamssavice.ssavice.book.entity.Book;
import teamssavice.ssavice.book.infrastructure.repository.BookRepository;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookReadService {

    private final BookRepository bookRepository;

    public Page<Book> findAllByUserId(Long userId, Pageable pageable) {

        return bookRepository.findAllByUserId(userId, pageable);
    }

    public Page<Book> findAllByUserIdAndStatus(Long userId, BookStatus status, Pageable pageable) {
        return bookRepository.findAllByUserIdAndStatus(userId, status, pageable);
    }
}
