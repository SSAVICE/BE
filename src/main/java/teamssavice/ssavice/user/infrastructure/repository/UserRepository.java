package teamssavice.ssavice.user.infrastructure.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import teamssavice.ssavice.user.entity.Users;


public interface UserRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByEmail(String email);

    @Query("SELECT u FROM Users u LEFT JOIN FETCH u.imageResource where u.id = :id")
    Optional<Users> findByIdFetchJoinImageResource(@Param("id") Long id);

    @Query("SELECT u FROM Users u LEFT JOIN FETCH u.address WHERE u.id = :id")
    Optional<Users> findByIdFetchJoinAddress(@Param("id") Long id);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM Users u LEFT JOIN FETCH u.address LEFT JOIN FETCH u.imageResource WHERE u.id = :id")
    Optional<Users> findByIdFetchJoinAddressAndImageResource(@Param("id") Long id);

}
