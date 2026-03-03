package teamssavice.ssavice.company.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import teamssavice.ssavice.company.entity.Company;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    @Query("SELECT c FROM Company c JOIN FETCH c.address WHERE c.id = :id")
    Optional<Company> findByCompanyIdFetchJoinAddress(@Param("id") Long id);

    @Query("SELECT c FROM Company c LEFT JOIN FETCH c.imageResource where c.id = :id")
    Optional<Company> findByIdFetchJoinImageResource(@Param("id") Long id);

    @Query("SELECT c FROM Company c JOIN FETCH c.address LEFT JOIN FETCH c.imageResource where c.id = :id")
    Optional<Company> findByIdFetchJoinAddressAndImageResource(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Company c SET c.ratingSum = c.ratingSum + :score, c.rateCount = c.rateCount + 1 WHERE c.id = :id")
    int addRating(@Param("id") Long id, @Param("score") Integer score);
}
