package teamssavice.ssavice.company.infrastructure.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import teamssavice.ssavice.company.entity.Company;
import teamssavice.ssavice.user.entity.Users;
import java.util.Optional;

public interface CompanyRepository extends CrudRepository<Company, Long> {
    boolean existsByUser(Users user);
    Optional<Company> findByUser(Users user);

    Optional<Company> findByUserId(Long userId);
    @Query("SELECT c FROM Company c JOIN FETCH c.address WHERE c.id = :id")
    Optional<Company> findByCompanyIdFetchJoin(@Param("id") Long id);
}
