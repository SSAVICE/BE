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

    public UserModel.Login refresh(String refreshToken) {
        RefreshToken entity = getRefreshToken(refreshToken);
        Users user = userRepository.findById(entity.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        Token token = tokenProvider.createToken(user.getId(), user.getRole());
        entity.refresh();
        saveRefreshToken(user.getId(), token);
        return UserModel.Login.from(token);
    }

    public void saveRefreshToken(Long userId, Token token) {
        String hashed = tokenProvider.hashRefreshToken(token.refreshToken());
        RefreshToken refreshToken = RefreshToken.builder().userId(userId).issuedAt(new Date()).expiresIn(token.expiresIn()).build();
        refreshTokenMap.put(hashed, refreshToken);
    }

    public RefreshToken getRefreshToken(String refreshToken) {
        String hashed = tokenProvider.hashRefreshToken(refreshToken);
        if(!refreshTokenMap.containsKey(hashed)) throw new AuthenticationException("유효하지 않은 토큰입니다.");
        RefreshToken token = refreshTokenMap.get(hashed);
        if(token.isRevoked()) throw new AuthenticationException("유효하지 않은 토큰입니다.");
        if(token.isExpired()) throw new AuthenticationException("만료된 토큰입니다.");

        return token;
    }
}
