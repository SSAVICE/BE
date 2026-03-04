package teamssavice.ssavice.fixture;

import org.springframework.test.util.ReflectionTestUtils;
import teamssavice.ssavice.account.constants.Provider;
import teamssavice.ssavice.account.entity.Account;
import teamssavice.ssavice.auth.constants.Role;

public class AccountFixture {

    public static Account account(Long id, Role role) {
        Account account = Account.builder()
            .provider(Provider.KAKAO)
            .providerId(String.valueOf(id))
            .role(role)
            .build();
        ReflectionTestUtils.setField(account, "id", id);
        return account;
    }

    public static Account userAccount(Long id) {
        return account(id, Role.USER);
    }

    public static Account companyAccount(Long id) {
        return account(id, Role.COMPANY);
    }
}
