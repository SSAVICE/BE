package teamssavice.ssavice.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.auth.Token;
import teamssavice.ssavice.auth.constants.Role;
import teamssavice.ssavice.auth.service.TokenService;
import teamssavice.ssavice.global.constants.ErrorCode;
import teamssavice.ssavice.global.exception.EntityNotFoundException;
import teamssavice.ssavice.user.constants.Provider;
import teamssavice.ssavice.user.constants.UserRole;
import teamssavice.ssavice.auth.RefreshToken;
import teamssavice.ssavice.user.entity.Users;
import teamssavice.ssavice.user.infrastructure.repository.UserRepository;
import teamssavice.ssavice.user.service.dto.UserModel;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final TokenService tokenService;

    @Transactional
    public UserModel.Login register(String kakaoToken) {
        // 토큰 검증
        String email = "test@test.com";

        // user 저장 및 중복 체크
        Users user = findByEmail(email)
                .orElseGet(() -> save(email));

        // 토큰 발행
        Token token = tokenService.issueToken(user.getId(), Role.USER);
        return UserModel.Login.from(token);
    }

    public UserModel.Login refresh(String refreshToken) {
        RefreshToken entity = tokenService.getRefreshToken(refreshToken);

        if (!userRepository.existsById(entity.getUserId())) {
            throw new EntityNotFoundException(ErrorCode.USER_NOT_FOUND);
        }
        Token token = tokenService.refresh(entity);
        return UserModel.Login.from(token);
    }

    public Optional<Users> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Users save(String email) {
        Users user = Users.builder()
                .userRole(UserRole.USER)
                .provider(Provider.KAKAO)
                .name("홍길동")
                .email(email)
                .phoneNumber("010-1234-5678")
                .imageUrl("url")
                .build();
        userRepository.save(user);
        return user;
    }

}
