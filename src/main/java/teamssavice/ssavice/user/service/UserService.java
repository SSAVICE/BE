package teamssavice.ssavice.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import teamssavice.ssavice.global.auth.JwtToken;
import teamssavice.ssavice.user.Users;
import teamssavice.ssavice.user.infrastructure.repository.UserRepository;
import teamssavice.ssavice.user.service.dto.UserModel;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserModel.Login register(String token) {
        // 토큰 검증
        String email = "test@test.com";

        // user 저장 및 중복 체크
        Users user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    Users newUser = Users.builder().build();
                    userRepository.save(newUser);
                    return newUser;
                });


        // jwt 토큰 발행
        JwtToken jwt = new JwtToken("accessToken", 3600L, "refreshToken");

        return UserModel.Login.from(jwt);
    }
}
