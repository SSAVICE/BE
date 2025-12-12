package teamssavice.ssavice.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import teamssavice.ssavice.global.auth.JwtProvider;
import teamssavice.ssavice.global.auth.JwtToken;
import teamssavice.ssavice.user.Users;
import teamssavice.ssavice.user.constants.Provider;
import teamssavice.ssavice.user.constants.Role;
import teamssavice.ssavice.user.infrastructure.repository.UserRepository;
import teamssavice.ssavice.user.service.dto.UserModel;

@Service
@RequiredArgsConstructor
public class UserService {
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    public UserModel.Login register(String token) {
        // 토큰 검증
        String email = "test@test.com";

        // user 저장 및 중복 체크
        Users user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    Users newUser = Users.builder()
                            .role(Role.USER)
                            .provider(Provider.KAKAO)
                            .name("홍길동")
                            .email(email)
                            .phoneNumber("010-1234-5678")
                            .imageUrl("url")
                            .build();
                    userRepository.save(newUser);
                    return newUser;
                });

        // jwt 토큰 발행
        JwtToken jwt = jwtProvider.createToken(user.getId(), user.getRole());
        return UserModel.Login.from(jwt);
    }
}
