package teamssavice.ssavice.fixture;

import teamssavice.ssavice.user.constants.Provider;
import teamssavice.ssavice.user.constants.UserRole;
import teamssavice.ssavice.user.entity.Users;

public class UserFixture {

    public static Users user() {
        return Users.builder()
                .userRole(UserRole.USER)
                .provider(Provider.KAKAO)
                .name("user")
                .email("user@email.com")
                .phoneNumber("010-1234-5678")
                .imageResource(null)
                .build();
    }

    public static Users of(UserRole userRole, String name, String email, String phoneNumber) {
        return Users.builder()
                .userRole(userRole)
                .provider(Provider.KAKAO)
                .name(name)
                .email(email)
                .phoneNumber(phoneNumber)
                .imageResource(null)
                .build();
    }
}
