package teamssavice.ssavice.fixture;

import teamssavice.ssavice.account.constants.Provider;
import teamssavice.ssavice.account.entity.Account;
import teamssavice.ssavice.auth.constants.Role;
import teamssavice.ssavice.user.constants.UserRole;
import teamssavice.ssavice.user.entity.Users;

public class UserFixture {

    public static Account account() {
        return Account.builder()
                .provider(Provider.KAKAO)
                .providerId("1234567890")
                .role(Role.USER)
                .build();
    }

    public static Users user(Account account) {
        return Users.builder()
                .account(account)
                .userRole(UserRole.USER)
                .name("user")
                .email("user@email.com")
                .phoneNumber("010-1234-5678")
                .imageResource(null)
                .build();
    }

    public static Users user() {
        Account account = account();
        return Users.builder()
                .account(account)
                .userRole(UserRole.USER)
                .name("user")
                .email("user@email.com")
                .phoneNumber("010-1234-5678")
                .imageResource(null)
                .build();
    }

    public static Users of(UserRole userRole, String name, String email, String phoneNumber) {
        Account account = Account.builder()
                .provider(Provider.KAKAO)
                .providerId(String.valueOf(System.nanoTime()))
                .role(Role.USER)
                .build();
        return Users.builder()
                .account(account)
                .userRole(userRole)
                .name(name)
                .email(email)
                .phoneNumber(phoneNumber)
                .imageResource(null)
                .build();
    }
}
