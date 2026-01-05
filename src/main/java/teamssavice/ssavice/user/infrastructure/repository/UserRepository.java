package teamssavice.ssavice.user.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import teamssavice.ssavice.user.entity.Users;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);

    @Query("SELECT u FROM Users u LEFT JOIN FETCH u.imageResource where u.id = :id")
    Optional<Users> findByIdFetchJoinImageResource(@Param("id") Long id);
}
