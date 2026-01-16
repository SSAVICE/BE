package teamssavice.ssavice.book.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.book.constants.BookStatus;
import teamssavice.ssavice.book.entity.Book;
import teamssavice.ssavice.book.infrastructure.repository.BookRepository;
import teamssavice.ssavice.serviceItem.constants.ServiceStatus;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;
import teamssavice.ssavice.user.entity.Users;


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

    // 취소한 사람은 다시 신청이 가능
    public boolean existsByUserAndServiceItem(Users user, ServiceItem serviceItem) {
        return bookRepository.existsByUserIdAndServiceItemIdAndBookStatusNot(user.getId(), serviceItem.getId(), BookStatus.CANCELED);
    }

    public Long countByUserIdAndBookStatusAndServiceStatus(Long userId, BookStatus bookStatus, ServiceStatus serviceStatus) {
        return bookRepository.countByUserIdAndBookStatusAndServiceStatus(userId, bookStatus, serviceStatus);
    }
}
