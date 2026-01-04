package teamssavice.ssavice.user.infrastructure.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import teamssavice.ssavice.user.entity.Users;

public interface UserRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByEmail(String email);

    @Query("SELECT u FROM Users u JOIN FETCH u.address WHERE u.id = :id")
    Optional<Users> findByIdFetchJoinAddress(@Param("id") Long id);
}
