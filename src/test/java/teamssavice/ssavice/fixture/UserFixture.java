package teamssavice.ssavice.fixture;

import teamssavice.ssavice.user.constants.Provider;
import teamssavice.ssavice.user.constants.UserRole;
import teamssavice.ssavice.user.entity.Users;

public class UserFixture {

    public static Users user() {
        return Users.builder()
                .userRole(UserRole.USER)
                .provider(Provider.KAKAO)
                .providerId("1234567890")
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
                .providerId(String.valueOf(System.nanoTime()))
                .name(name)
                .email(email)
                .phoneNumber(phoneNumber)
                .imageResource(null)
                .build();
    }
}
