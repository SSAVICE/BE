package teamssavice.ssavice.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import teamssavice.ssavice.auth.Token;
import teamssavice.ssavice.auth.constants.Role;
import teamssavice.ssavice.auth.service.TokenService;
import teamssavice.ssavice.global.constants.ErrorCode;
import teamssavice.ssavice.global.exception.EntityNotFoundException;
import teamssavice.ssavice.user.constants.Provider;
import teamssavice.ssavice.user.constants.UserRole;
import teamssavice.ssavice.user.entity.RefreshToken;
import teamssavice.ssavice.user.entity.Users;
import teamssavice.ssavice.user.infrastructure.repository.UserRepository;
import teamssavice.ssavice.user.service.dto.UserModel;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final TokenService tokenService;

    public UserModel.Login register(String kakaoToken) {
        // 토큰 검증
        String email = "test@test.com";

        // user 저장 및 중복 체크
        Users user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    Users newUser = Users.builder()
                            .userRole(UserRole.USER)
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
        Token token = tokenService.createToken(user.getId(), Role.USER);
        tokenService.saveRefreshToken(user.getId(), token, Role.USER);

        return UserModel.Login.from(token);
    }

    public UserModel.Login refresh(String refreshToken) {
        RefreshToken entity = tokenService.getRefreshToken(refreshToken);
        Users user = userRepository.findById(entity.getUserId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));

        Token token = tokenService.createToken(user.getId(), entity.getRole());
        entity.refresh();
        tokenService.saveRefreshToken(user.getId(), token, entity.getRole());
        return UserModel.Login.from(token);
    }

}
