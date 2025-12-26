package teamssavice.ssavice.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import teamssavice.ssavice.auth.Token;
import teamssavice.ssavice.auth.TokenProvider;
import teamssavice.ssavice.auth.constants.Role;
import teamssavice.ssavice.fixture.TokenFixture;
import teamssavice.ssavice.fixture.UserFixture;
import teamssavice.ssavice.auth.RefreshToken;
import teamssavice.ssavice.global.exception.AuthenticationException;
import teamssavice.ssavice.user.entity.Users;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class TokenServiceTest {
    @InjectMocks
    private TokenService tokenService;
    @Mock
    private TokenProvider tokenProvider;

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
        tokenService.saveRefreshToken(user.getId(), token, Role.USER);
        RefreshToken actual = tokenService.getRefreshToken(token.refreshToken());

        // then
        assertAll(
                () -> assertThat(actual.getSubject()).isEqualTo(user.getId()),
                () -> assertThat(actual.isRevoked()).isEqualTo(false)
        );
    }

    @Test
    @DisplayName("리프레쉬 토큰 조회 테스트")
    void getRefreshTokenTest() {
        // given
        Users user = this.user;
        Token token = this.token;
        String hashed = "hashedRefreshToken";

        // when
        when(tokenProvider.hashRefreshToken(token.refreshToken())).thenReturn(hashed);
        tokenService.saveRefreshToken(user.getId(), token, Role.USER);
        RefreshToken actual = tokenService.getRefreshToken(token.refreshToken());

        // then
        assertThat(actual.getSubject()).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("토큰 갱신 테스트")
    void refreshTest() {
        // given
        Users user = this.user;
        Token token = this.token;
        String hashed = "hashedRefreshToken";
        Token newToken = TokenFixture.of("newAccessToken", 3600L, "newRefreshToken");
        String newHashed = "newHashedRefreshToken";
        when(tokenProvider.hashRefreshToken(token.refreshToken())).thenReturn(hashed);
        when(tokenProvider.createToken(user.getId(), Role.USER)).thenReturn(token);
        tokenService.saveRefreshToken(user.getId(), token, Role.USER);

        // when
        when(tokenProvider.hashRefreshToken(newToken.refreshToken())).thenReturn(newHashed);
        when(tokenProvider.createToken(user.getId(), Role.USER)).thenReturn(newToken);
        var actual = tokenService.refresh(token.refreshToken());


        // then
        assertThrows(AuthenticationException.class, () -> tokenService.getRefreshToken(token.refreshToken()));
        assertAll(
                () -> assertThat(actual.accessToken()).isEqualTo(newToken.accessToken()),
                () -> assertThat(actual.refreshToken()).isEqualTo(newToken.refreshToken())
        );
    }
}