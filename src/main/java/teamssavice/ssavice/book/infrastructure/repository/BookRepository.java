package teamssavice.ssavice.book.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import teamssavice.ssavice.book.entity.Book;
import teamssavice.ssavice.book.entity.BookStatus;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;

import java.util.List;
import java.util.Optional;


@Repository
public interface BookRepository extends JpaRepository<Book, Long>, BookRepositoryCustom {

    boolean existsByUserIdAndServiceItemIdAndBookStatusNot(Long userId, Long serviceItemId, BookStatus bookStatus);

    @Query("SELECT b FROM Book b " +
            "JOIN b.serviceItem s " + // 페치 조인이 아닌 일반 조인
            "WHERE b.user.id = :userId " +
            "AND b.bookStatus = :bookStatus")
    List<Book> findByUserIdAndBookStatus(
            @Param("userId") Long userId,
            @Param("bookStatus") BookStatus bookStatus
    );

    List<Book> findAllByServiceItemIdAndBookStatus(Long serviceItemId, BookStatus bookStatus);

    Optional<Book> findFirstByUserIdAndServiceItemIdOrderByCreatedAtDesc(Long userId, Long serviceItemId);

}
