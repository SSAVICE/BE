package teamssavice.ssavice.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import teamssavice.ssavice.auth.Token;
import teamssavice.ssavice.auth.TokenProvider;
import teamssavice.ssavice.auth.constants.Role;
import teamssavice.ssavice.auth.service.TokenService;
import teamssavice.ssavice.fixture.TokenFixture;
import teamssavice.ssavice.fixture.UserFixture;
import teamssavice.ssavice.global.exception.AuthenticationException;
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
    private UserService userService;
    private TokenService tokenService;
    @Mock
    private TokenProvider tokenProvider;
    @Mock
    private UserRepository userRepository;


    private Users user;
    private Token token;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService(tokenProvider);
        userService = new UserService(userRepository, tokenService);
        user = UserFixture.user();
        token = TokenFixture.token();
    }

    @Test
    @DisplayName("토큰 갱신 시 이전 토큰 취소 테스트")
    void refreshRevokeTest() {
        // given
        Users user = this.user;
        Token token = this.token;
        Token newToken = TokenFixture.of("newAccessToken", 3600L, "newRefreshToken");
        String hashed = "hashedRefreshToken";
        String newHashed = "newHashedRefreshToken";
        when(tokenProvider.hashRefreshToken(token.refreshToken())).thenReturn(hashed);
        tokenService.saveRefreshToken(user.getId(), token, Role.USER);

        // when
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(tokenProvider.createToken(user.getId(), Role.USER)).thenReturn(newToken);
        when(tokenProvider.hashRefreshToken(newToken.refreshToken())).thenReturn(newHashed);
        UserModel.Login actual = userService.refresh(token.refreshToken());

        // then
        assertThrows(AuthenticationException.class, () -> tokenService.getRefreshToken(token.refreshToken()));
        assertAll(
                () -> assertThat(actual.accessToken()).isEqualTo(newToken.accessToken()),
                () -> assertThat(actual.refreshToken()).isEqualTo(newToken.refreshToken())
        );
    }
}