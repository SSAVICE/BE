package teamssavice.ssavice.book.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.book.constants.BookStatusFilter;
import teamssavice.ssavice.book.entity.BookStatus;
import teamssavice.ssavice.book.entity.Book;
import teamssavice.ssavice.book.infrastructure.repository.BookRepository;
import teamssavice.ssavice.global.constants.ErrorCode;
import teamssavice.ssavice.global.exception.EntityNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookReadService {

    private final BookRepository bookRepository;

    public Page<Book> findAllByUserIdAndStatus(Long userId, BookStatusFilter status, Pageable pageable) {

        return bookRepository.findAllByUserIdAndStatus(userId, status, pageable);
    }

    // 취소한 사람은 다시 신청이 가능
    public boolean existsByUserAndServiceAndStatusNot(Long userId, Long serviceId, BookStatus status) {
        return bookRepository.existsByUserIdAndServiceItemIdAndBookStatusNot(userId, serviceId, status);
    }

    public List<Book> findByUserIdAndBookStatus(Long userId, BookStatus bookStatus) {
        return bookRepository.findByUserIdAndBookStatus(userId, bookStatus);
    }

    public List<Book> findAllByServiceItemIdAndBookStatus(Long serviceItemId, BookStatus bookStatus) {
        return bookRepository.findAllByServiceItemIdAndBookStatus(serviceItemId, bookStatus);
    }


    public Book findFirstByUserIdAndServiceItemIdOrderByCreatedAtDesc(Long userId, Long serviceItemId) {
        return bookRepository.findFirstByUserIdAndServiceItemIdOrderByCreatedAtDesc(userId, serviceItemId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.BOOKING_NOT_FOUND));
    }
}
