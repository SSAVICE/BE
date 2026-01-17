package teamssavice.ssavice.book.infrastructure.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import teamssavice.ssavice.book.constants.BookStatusFilter;
import teamssavice.ssavice.book.entity.Book;

public interface BookRepositoryCustom {
    Page<Book> findAllByUserIdAndStatus(Long userId, BookStatusFilter status, Pageable pageable);
}
