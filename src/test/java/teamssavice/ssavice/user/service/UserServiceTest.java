package teamssavice.ssavice.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import teamssavice.ssavice.global.auth.Token;
import teamssavice.ssavice.global.auth.TokenProvider;
import teamssavice.ssavice.global.exception.AuthenticationException;
import teamssavice.ssavice.user.TokenFixture;
import teamssavice.ssavice.user.UserFixture;
import teamssavice.ssavice.user.entity.RefreshToken;
import teamssavice.ssavice.user.entity.Users;
import teamssavice.ssavice.user.infrastructure.repository.UserRepository;
import teamssavice.ssavice.user.service.dto.UserModel;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserService userService;
    @Mock
    private TokenProvider tokenProvider;
    @Mock
    private UserRepository userRepository;

    private Users user;
    private Token token;

    @BeforeEach
    void setUp() {
        user = UserFixture.user();
        token = TokenFixture.token();
    }

    @Test
    @DisplayName("리프레쉬 토큰 저장 테스트")
    void saveRefreshTokenTest() {
        // given
        Users user = this.user;
        Token token = this.token;
        String hashed = "hashedRefreshToken";

        // when
        when(tokenProvider.hashRefreshToken(token.refreshToken())).thenReturn(hashed);
        userService.saveRefreshToken(user.getId(), token);
        RefreshToken actual = userService.getRefreshToken(token.refreshToken());

        // then
        assertAll(
                () -> assertThat(actual.getUserId()).isEqualTo(user.getId()),
                () -> assertThat(actual.isRevoked()).isEqualTo(false)
        );
    }

    @Test
    @DisplayName("토큰 갱신 시 이전 토큰 취소 테스트")
    void revokeRefreshTokenTest() {
        // given
        Users user = this.user;
        Token token = this.token;
        Token newToken = TokenFixture.of("newAccessToken", 3600L, "newRefreshToken");
        String hashed = "hashedRefreshToken";
        String newHashed = "newHashedRefreshToken";
        when(tokenProvider.hashRefreshToken(token.refreshToken())).thenReturn(hashed);
        userService.saveRefreshToken(user.getId(), token);

        // when
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(tokenProvider.createToken(user.getId(), user.getRole())).thenReturn(newToken);
        when(tokenProvider.hashRefreshToken(newToken.refreshToken())).thenReturn(newHashed);
        UserModel.Login actual = userService.refresh(token.refreshToken());

        // then
        assertThrows(AuthenticationException.class, () -> userService.getRefreshToken(token.refreshToken()));
        assertAll(
                () -> assertThat(actual.accessToken()).isEqualTo(newToken.accessToken()),
                () -> assertThat(actual.refreshToken()).isEqualTo(newToken.refreshToken())
        );
    }
}