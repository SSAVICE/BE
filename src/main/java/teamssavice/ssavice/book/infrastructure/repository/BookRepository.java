package teamssavice.ssavice.book.infrastructure.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import teamssavice.ssavice.book.entity.Book;


@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("SELECT b FROM Book b " +
            "JOIN FETCH b.user " +
            "JOIN FETCH b.serviceItem s " +
            "JOIN FETCH s.company " +
            "JOIN FETCH s.address " +
            "WHERE b.user.id = :userId")
    Page<Book> findAllByUserId(@Param("userId") Long userId, Pageable pageable);

}
