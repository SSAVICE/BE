package teamssavice.ssavice.book.infrastructure.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import teamssavice.ssavice.book.constants.BookStatus;
import teamssavice.ssavice.book.entity.Book;
import teamssavice.ssavice.serviceItem.constants.ServiceStatus;


@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    @Query(value = "SELECT b FROM Book b " +
            "JOIN FETCH b.user " +
            "JOIN FETCH b.serviceItem s " +
            "JOIN FETCH s.company " +
            "JOIN FETCH s.address " +
            "WHERE b.user.id = :userId",

            countQuery = "SELECT count(b) FROM Book b WHERE b.user.id = :userId")
    Page<Book> findAllByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query(
        value = "SELECT b FROM Book b " +
            "JOIN FETCH b.user " +
            "JOIN FETCH b.serviceItem s " +
            "JOIN FETCH s.company " +
            "JOIN FETCH s.address " +
            "WHERE b.user.id = :userId " +
            "AND (:status IS NULL OR b.bookStatus = :status)",
        countQuery = "SELECT count(b) FROM Book b " +
            "WHERE b.user.id = :userId " +
            "AND (:status IS NULL OR b.bookStatus = :status)"
    )
    Page<Book> findAllByUserIdAndStatus(Long userId, BookStatus status, Pageable pageable);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE Book b SET b.bookStatus = :targetStatus " +
            "WHERE b.serviceItem.id = :serviceId " +
            "AND b.bookStatus = :currentStatus")
    int updateStatusByServiceId(
            @Param("serviceId") Long serviceId,
            @Param("currentStatus") BookStatus currentStatus,
            @Param("targetStatus") BookStatus targetStatus
    );

    boolean existsByUserIdAndServiceItemIdAndBookStatusNot(Long userId, Long serviceItemId, BookStatus bookStatus);

    @Query("SELECT COUNT(b) FROM Book b " +
            "JOIN b.serviceItem s " + // 페치 조인이 아닌 일반 조인
            "WHERE b.user.id = :userId " +
            "AND b.bookStatus = :bookStatus " +
            "AND s.status = :serviceStatus")
    Long countByUserIdAndBookStatusAndServiceStatus(
            @Param("userId") Long userId,
            @Param("bookStatus") BookStatus bookStatus,
            @Param("serviceStatus") ServiceStatus serviceStatus
    );
}
