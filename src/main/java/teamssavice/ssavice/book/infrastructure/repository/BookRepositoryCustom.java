package teamssavice.ssavice.book.infrastructure.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import teamssavice.ssavice.book.constants.BookStatusFilter;
import teamssavice.ssavice.book.entity.Book;

import java.util.List;

public interface BookRepositoryCustom {
    Page<Book> findAllByUserIdAndStatus(Long userId, BookStatusFilter status, Pageable pageable);

    Page<Book> findAllByCompanyIdAndStatus(Long company, BookStatusFilter status, Pageable pageable);

    List<Book> findLatestBooksByUserIdAndServiceItemId(Long userId, List<Long> serviceItemIds);
    // (user_id, service_item_id, id DESC) 인덱스 추가
}
