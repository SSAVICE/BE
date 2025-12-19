package teamssavice.ssavice.company.infrastructure.repository;

import org.springframework.data.repository.CrudRepository;
import teamssavice.ssavice.company.entity.Company;

public interface CompanyRepository extends CrudRepository<Company, Long> {
    boolean existsByUserId(Long userId);
}
