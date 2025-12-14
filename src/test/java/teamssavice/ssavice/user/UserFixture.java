package teamssavice.ssavice.user;

import teamssavice.ssavice.user.constants.Provider;
import teamssavice.ssavice.user.constants.Role;
import teamssavice.ssavice.user.entity.Users;

public class UserFixture {

    public static Users user() {
        return Users.builder()
                .role(Role.USER)
                .provider(Provider.KAKAO)
                .name("user")
                .email("user@email.com")
                .phoneNumber("010-1234-5678")
                .imageUrl("imageUrl")
                .build();
    }

    public static Users of(Role role, String name, String email, String phoneNumber, String imageUrl) {
        return Users.builder()
                .role(role)
                .provider(Provider.KAKAO)
                .name(name)
                .email(email)
                .phoneNumber(phoneNumber)
                .imageUrl(imageUrl)
                .build();
    }
}
