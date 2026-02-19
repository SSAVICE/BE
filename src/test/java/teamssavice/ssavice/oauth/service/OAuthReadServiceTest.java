package teamssavice.ssavice.oauth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import teamssavice.ssavice.global.constants.ErrorCode;
import teamssavice.ssavice.global.exception.AuthenticationException;
import teamssavice.ssavice.oauth.service.client.OAuthClient;
import teamssavice.ssavice.oauth.service.client.OAuthUserInfo;
import teamssavice.ssavice.user.constants.Provider;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OAuthReadServiceTest {

    private OAuthClient kakaoClient;
    private OAuthReadService oAuthReadService;

    @BeforeEach
    void setUp() {
        kakaoClient = mock(OAuthClient.class);
        given(kakaoClient.getProvider()).willReturn(Provider.KAKAO);

        oAuthReadService = new OAuthReadService(List.of(kakaoClient));
    }

    @Nested
    @DisplayName("getUserInfo 메서드")
    class GetUserInfo {

        @Test
        @DisplayName("성공: 등록된 provider로 요청하면 해당 OAuthClient를 통해 사용자 정보를 반환한다")
        void success() {
            // given
            String accessToken = "kakao-access-token";
            OAuthUserInfo expected = OAuthUserInfo.builder()
                    .providerId("12345678")
                    .email("user@kakao.com")
                    .name("홍길동")
                    .phoneNumber("010-1234-5678")
                    .build();

            given(kakaoClient.getUserInfo(accessToken)).willReturn(expected);

            // when
            OAuthUserInfo result = oAuthReadService.getUserInfo(Provider.KAKAO, accessToken);

            // then
            assertThat(result.providerId()).isEqualTo("12345678");
            assertThat(result.email()).isEqualTo("user@kakao.com");
            assertThat(result.name()).isEqualTo("홍길동");
            assertThat(result.phoneNumber()).isEqualTo("010-1234-5678");
            verify(kakaoClient).getUserInfo(accessToken);
        }

        @Test
        @DisplayName("성공: OAuthClient에 accessToken을 그대로 전달한다")
        void success_passesAccessTokenToClient() {
            // given
            String accessToken = "specific-token-value";
            OAuthUserInfo userInfo = OAuthUserInfo.builder()
                    .providerId("99")
                    .email("test@test.com")
                    .name("테스터")
                    .phoneNumber("010-0000-0000")
                    .build();
            given(kakaoClient.getUserInfo(accessToken)).willReturn(userInfo);

            // when
            oAuthReadService.getUserInfo(Provider.KAKAO, accessToken);

            // then
            verify(kakaoClient).getUserInfo(accessToken);
        }

        @Test
        @DisplayName("실패: 등록되지 않은 provider로 요청하면 UNSUPPORTED_PROVIDER 예외를 던진다")
        void fail_whenUnsupportedProvider() {
            // given
            // OAuthReadService는 KAKAO 클라이언트만 등록되어 있음
            // Provider에 KAKAO만 존재하므로, 등록되지 않은 provider를 시뮬레이션하기 위해
            // clients map에 없는 케이스를 만든다
            OAuthReadService emptyService = new OAuthReadService(List.of());
            String accessToken = "kakao-access-token";

            // when & then
            assertThatThrownBy(() -> emptyService.getUserInfo(Provider.KAKAO, accessToken))
                    .isInstanceOf(AuthenticationException.class)
                    .satisfies(ex -> assertThat(((AuthenticationException) ex).getErrorCode())
                            .isEqualTo(ErrorCode.UNSUPPORTED_PROVIDER));
        }
    }
}
