package teamssavice.ssavice.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import teamssavice.ssavice.global.auth.Token;
import teamssavice.ssavice.global.auth.TokenProvider;
import teamssavice.ssavice.global.exception.AuthenticationException;
import teamssavice.ssavice.global.exception.EntityNotFoundException;
import teamssavice.ssavice.user.constants.Provider;
import teamssavice.ssavice.user.constants.Role;
import teamssavice.ssavice.user.entity.RefreshToken;
import teamssavice.ssavice.user.entity.Users;
import teamssavice.ssavice.user.infrastructure.repository.UserRepository;
import teamssavice.ssavice.user.service.dto.UserModel;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class UserService {
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;

    private final Map<String, RefreshToken> refreshTokenMap = new ConcurrentHashMap<>();

    public UserModel.Login register(String kakaoToken) {
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

        // 토큰 발행
        Token token = tokenProvider.createToken(user.getId(), user.getRole());
        saveRefreshToken(user.getId(), token);

        return UserModel.Login.from(token);
    }


    private void saveRefreshToken(Long userId, Token token) {
        String hashed = tokenProvider.hashRefreshToken(token.refreshToken());
        RefreshToken refreshToken = RefreshToken.builder().userId(userId).issuedAt(new Date()).expiresIn(token.expiresIn()).build();
        refreshTokenMap.put(hashed, refreshToken);
    }

}
