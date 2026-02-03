package teamssavice.ssavice.book.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.book.constants.BookStatusFilter;
import teamssavice.ssavice.book.entity.Book;
import teamssavice.ssavice.book.entity.BookStatus;
import teamssavice.ssavice.book.infrastructure.repository.BookRepository;
import teamssavice.ssavice.global.constants.ErrorCode;
import teamssavice.ssavice.global.exception.EntityNotFoundException;
import teamssavice.ssavice.serviceItem.constants.ServiceStatus;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookReadService {

    private final BookRepository bookRepository;

    public Page<Book> findAllByUserIdAndStatus(Long userId, BookStatusFilter status, Pageable pageable) {
        return bookRepository.findAllByUserIdAndStatus(userId, status, pageable);
    }

    public Page<Book> findAllByCompanyIdAndStatus(Long userId, BookStatusFilter status, Pageable pageable) {
        return bookRepository.findAllByCompanyIdAndStatus(userId, status, pageable);
    }

    // 취소한 사람은 다시 신청이 가능
    public boolean existsByUserAndServiceAndStatusNot(Long userId, Long serviceId, BookStatus status) {
        return bookRepository.existsByUserIdAndServiceItemIdAndBookStatusNot(userId, serviceId, status);
    }

    public List<Book> findAllByServiceItemIdAndBookStatus(Long serviceItemId, BookStatus bookStatus) {
        return bookRepository.findAllByServiceItemIdAndBookStatus(serviceItemId, bookStatus);
    }

    public Book findFirstByUserIdAndServiceItemIdOrderByCreatedAtDesc(Long userId, Long serviceItemId) {
        return bookRepository.findFirstByUserIdAndServiceItemIdOrderByCreatedAtDesc(userId, serviceItemId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.BOOKING_NOT_FOUND));
    }

    public boolean isBookedByUserIdAndServiceId(Long userId, Long serviceItemId) {
        return bookRepository.findFirstByUserIdAndServiceItemIdOrderByCreatedAtDesc(userId, serviceItemId)
                .map(book -> book.getBookStatus() == BookStatus.RESERVED)
                .orElse(false);
    }

    public Long countRecruitingBooksByUserId(Long userId) {
        return bookRepository.countRecruitingBooksByUserId(userId, BookStatus.RESERVED, ServiceStatus.RECRUITING, LocalDateTime.now());
    }

    public Long countSucceededBooksByUserId(Long userId) {
        return bookRepository.countSucceededBooksByUserId(userId, BookStatus.RESERVED, ServiceStatus.SUCCEEDED, LocalDateTime.now());
    }

    public Long countRecruitingBooksByCompanyId(Long companyId) {
        return bookRepository.countRecruitingBooksByCompanyId(companyId, BookStatus.RESERVED, ServiceStatus.RECRUITING, LocalDateTime.now());
    }

    public Long countSucceededBooksByCompanyId(Long companyId) {
        return bookRepository.countSucceededBooksByCompanyId(companyId, BookStatus.RESERVED, ServiceStatus.SUCCEEDED, LocalDateTime.now());
    }
}
