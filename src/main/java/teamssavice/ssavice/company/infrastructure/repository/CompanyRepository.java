package teamssavice.ssavice.company.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import teamssavice.ssavice.company.entity.Company;
import teamssavice.ssavice.user.entity.Users;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    boolean existsByUser(Users user);
    Optional<Company> findByUser(Users user);

    Optional<Company> findByUserId(Long userId);

    @Query("SELECT c FROM Company c JOIN FETCH c.address WHERE c.id = :id")
    Optional<Company> findByCompanyIdFetchJoinAddress(@Param("id") Long id);

    @Query("SELECT c FROM Company c LEFT JOIN FETCH c.imageResource where c.id = :id")
    Optional<Company> findByIdFetchJoinImageResource(@Param("id") Long id);

    @Query("SELECT c FROM Company c JOIN FETCH c.address LEFT JOIN FETCH c.imageResource where c.id = :id")
    Optional<Company> findByIdFetchJoinAddressAndImageResource(@Param("id") Long id);
}
