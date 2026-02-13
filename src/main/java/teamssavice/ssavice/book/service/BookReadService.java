package teamssavice.ssavice.book.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.book.constants.BookStatusFilter;
import teamssavice.ssavice.book.entity.Book;
import teamssavice.ssavice.book.entity.BookStatus;
import teamssavice.ssavice.book.infrastructure.repository.BookRepository;
import teamssavice.ssavice.global.constants.ErrorCode;
import teamssavice.ssavice.global.exception.EntityNotFoundException;
import teamssavice.ssavice.serviceItem.constants.ServiceStatus;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


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

    public List<Book> findAllByServiceItemIdAndBookStatus(Long serviceItemId, BookStatus bookStatus) {
        return bookRepository.findAllByServiceItemIdAndBookStatus(serviceItemId, bookStatus);
    }

    public Book findFirstByUserIdAndServiceItemIdOrderByCreatedAtDesc(Long userId, Long serviceItemId) {
        return bookRepository.findFirstByUserIdAndServiceItemIdOrderByCreatedAtDesc(userId, serviceItemId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.BOOKING_NOT_FOUND));
    }

    public boolean isBookedByUserIdAndServiceId(@Nullable Long userId, Long serviceItemId) {
        if(userId == null) return false;
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

    public Set<Long> findReservedServiceItemIdsFromLatestBooks(@Nullable Long userId, List<ServiceItem> serviceItems) {
        if(userId == null) return Collections.emptySet();

        List<Long> serviceIds = serviceItems.stream()
                .map(ServiceItem::getId)
                .toList();

        return bookRepository.findLatestBooksByUserIdAndServiceItemId(userId, serviceIds).stream()
                .filter(book -> book.getBookStatus() == BookStatus.RESERVED)
                .map(book -> book.getServiceItem().getId())
                .collect(Collectors.toSet());
    }
  
    public Long countAllBooksByUserId(Long userId) {
        return bookRepository.countByUserId(userId);
    }

    public Page<Book> findAllParticipantsByServiceItemId(Long serviceItemId, Pageable pageable) {
        return bookRepository.findAllByServiceItemIdWithUserAndImageResource(serviceItemId, BookStatus.RESERVED, pageable);
    }
}
