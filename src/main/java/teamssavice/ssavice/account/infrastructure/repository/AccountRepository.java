package teamssavice.ssavice.account.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import teamssavice.ssavice.account.constants.Provider;
import teamssavice.ssavice.account.entity.Account;
import teamssavice.ssavice.auth.constants.Role;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByProviderIdAndProviderAndRole(String providerId, Provider provider, Role role);

    List<Account> findAllByIdIn(List<Long> ids);
}
