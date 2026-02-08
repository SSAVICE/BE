package teamssavice.ssavice.book.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import teamssavice.ssavice.book.entity.Book;
import teamssavice.ssavice.book.entity.BookStatus;
import teamssavice.ssavice.serviceItem.constants.ServiceStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface BookRepository extends JpaRepository<Book, Long>, BookRepositoryCustom {

    boolean existsByUserIdAndServiceItemIdAndBookStatusNot(Long userId, Long serviceItemId, BookStatus bookStatus);

    @Query("SELECT COUNT(b) FROM Book b " +
            "JOIN b.serviceItem s " +
            "WHERE b.user.id = :userId " +
            "AND b.bookStatus = :bookStatus " +
            "AND s.status = :recruiting " +
            "AND s.deadline > :now")
    Long countRecruitingBooksByUserId(Long userId, BookStatus bookStatus, ServiceStatus recruiting, LocalDateTime now);

    @Query("SELECT COUNT(b) FROM Book b " +
            "JOIN b.serviceItem s " +
            "WHERE b.user.id = :userId " +
            "AND b.bookStatus = :bookStatus " +
            "AND s.status = :succeeded " +
            "AND s.endDate > :now")
    Long countSucceededBooksByUserId(Long userId, BookStatus bookStatus, ServiceStatus succeeded, LocalDateTime now);

    List<Book> findAllByServiceItemIdAndBookStatus(Long serviceItemId, BookStatus bookStatus);

    Optional<Book> findFirstByUserIdAndServiceItemIdOrderByCreatedAtDesc(Long userId, Long serviceItemId);

    Long countByUserId(Long userId);

}
