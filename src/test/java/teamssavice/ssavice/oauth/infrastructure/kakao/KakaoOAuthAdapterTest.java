package teamssavice.ssavice.oauth.infrastructure.kakao;

import feign.FeignException;
import feign.Request;
import feign.RetryableException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import teamssavice.ssavice.global.constants.ErrorCode;
import teamssavice.ssavice.global.exception.AuthenticationException;
import teamssavice.ssavice.global.exception.ExternalApiException;
import teamssavice.ssavice.oauth.infrastructure.kakao.client.KakaoApiClient;
import teamssavice.ssavice.oauth.infrastructure.kakao.dto.KakaoUserResponse;
import teamssavice.ssavice.oauth.service.client.OAuthUserInfo;
import teamssavice.ssavice.user.constants.Provider;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class KakaoOAuthAdapterTest {

    @InjectMocks
    private KakaoOAuthAdapter kakaoOAuthAdapter;

    @Mock
    private KakaoApiClient kakaoApiClient;

    @Nested
    @DisplayName("getProvider 메서드")
    class GetProvider {

        @Test
        @DisplayName("성공: KAKAO provider를 반환한다")
        void success() {
            // when
            Provider provider = kakaoOAuthAdapter.getProvider();

            // then
            assertThat(provider).isEqualTo(Provider.KAKAO);
        }
    }

    @Nested
    @DisplayName("getUserInfo 메서드")
    class GetUserInfo {

        @Test
        @DisplayName("성공: 정상적인 카카오 응답에서 OAuthUserInfo를 반환한다")
        void success() {
            // given
            String accessToken = "valid-access-token";
            KakaoUserResponse response = createResponse(12345678L, "user@kakao.com", "홍길동");

            given(kakaoApiClient.getUserInfo("Bearer " + accessToken)).willReturn(response);

            // when
            OAuthUserInfo result = kakaoOAuthAdapter.getUserInfo(accessToken);

            // then
            assertThat(result.providerId()).isEqualTo("12345678");
            assertThat(result.email()).isEqualTo("user@kakao.com");
            assertThat(result.name()).isEqualTo("홍길동");
            // 전화번호는 카카오에서 제공하지 않으므로 어댑터에서 기본값으로 고정
            assertThat(result.phoneNumber()).isEqualTo("010-0000-0000");
        }

        @Test
        @DisplayName("성공: accessToken에 'Bearer ' 접두사를 붙여서 카카오 API를 호출한다")
        void success_addsBearerPrefix() {
            // given
            String accessToken = "raw-token";
            KakaoUserResponse response = createResponse(1L, "a@b.com", "이름");

            given(kakaoApiClient.getUserInfo("Bearer raw-token")).willReturn(response);

            // when
            OAuthUserInfo result = kakaoOAuthAdapter.getUserInfo(accessToken);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("실패: id가 null이면 KAKAO_AUTH_FAILED 예외를 던진다")
        void fail_whenIdIsNull() {
            // given
            String accessToken = "token";
            KakaoUserResponse response = createResponse(null, "user@kakao.com", "홍길동");

            given(kakaoApiClient.getUserInfo("Bearer " + accessToken)).willReturn(response);

            // when & then
            assertThatThrownBy(() -> kakaoOAuthAdapter.getUserInfo(accessToken))
                    .isInstanceOf(AuthenticationException.class)
                    .satisfies(ex -> assertThat(((AuthenticationException) ex).getErrorCode())
                            .isEqualTo(ErrorCode.KAKAO_AUTH_FAILED));
        }

        @Test
        @DisplayName("실패: kakaoAccount가 null이면 KAKAO_INFO_NOT_PROVIDED 예외를 던진다")
        void fail_whenKakaoAccountIsNull() {
            // given
            String accessToken = "token";
            KakaoUserResponse response = new KakaoUserResponse();
            ReflectionTestUtils.setField(response, "id", 1L);
            // kakaoAccount를 설정하지 않으면 null

            given(kakaoApiClient.getUserInfo("Bearer " + accessToken)).willReturn(response);

            // when & then
            assertThatThrownBy(() -> kakaoOAuthAdapter.getUserInfo(accessToken))
                    .isInstanceOf(AuthenticationException.class)
                    .satisfies(ex -> assertThat(((AuthenticationException) ex).getErrorCode())
                            .isEqualTo(ErrorCode.KAKAO_INFO_NOT_PROVIDED));
        }

        @Test
        @DisplayName("실패: 이메일이 null이면 KAKAO_INFO_NOT_PROVIDED 예외를 던진다")
        void fail_whenEmailIsNull() {
            // given
            String accessToken = "token";
            KakaoUserResponse response = createResponse(1L, null, "홍길동");

            given(kakaoApiClient.getUserInfo("Bearer " + accessToken)).willReturn(response);

            // when & then
            assertThatThrownBy(() -> kakaoOAuthAdapter.getUserInfo(accessToken))
                    .isInstanceOf(AuthenticationException.class)
                    .satisfies(ex -> assertThat(((AuthenticationException) ex).getErrorCode())
                            .isEqualTo(ErrorCode.KAKAO_INFO_NOT_PROVIDED));
        }

        @Test
        @DisplayName("실패: profile이 null이면 KAKAO_INFO_NOT_PROVIDED 예외를 던진다")
        void fail_whenProfileIsNull() {
            // given
            String accessToken = "token";
            // profile 없이 account만 생성
            KakaoUserResponse response = createResponseWithNullProfile(1L, "user@kakao.com");

            given(kakaoApiClient.getUserInfo("Bearer " + accessToken)).willReturn(response);

            // when & then
            assertThatThrownBy(() -> kakaoOAuthAdapter.getUserInfo(accessToken))
                    .isInstanceOf(AuthenticationException.class)
                    .satisfies(ex -> assertThat(((AuthenticationException) ex).getErrorCode())
                            .isEqualTo(ErrorCode.KAKAO_INFO_NOT_PROVIDED));
        }

        @Test
        @DisplayName("실패: nickname이 null이면 KAKAO_INFO_NOT_PROVIDED 예외를 던진다")
        void fail_whenNicknameIsNull() {
            // given
            String accessToken = "token";
            KakaoUserResponse response = createResponse(1L, "user@kakao.com", null);

            given(kakaoApiClient.getUserInfo("Bearer " + accessToken)).willReturn(response);

            // when & then
            assertThatThrownBy(() -> kakaoOAuthAdapter.getUserInfo(accessToken))
                    .isInstanceOf(AuthenticationException.class)
                    .satisfies(ex -> assertThat(((AuthenticationException) ex).getErrorCode())
                            .isEqualTo(ErrorCode.KAKAO_INFO_NOT_PROVIDED));
        }

        @Test
        @DisplayName("실패: FeignException.Unauthorized 발생 시 KAKAO_AUTH_FAILED 예외를 던진다")
        void fail_whenUnauthorized() {
            // given
            String accessToken = "expired-token";
            Request request = Request.create(
                    Request.HttpMethod.GET, "https://kapi.kakao.com/v2/user/me",
                    Map.of(), null, Charset.defaultCharset(), null
            );
            FeignException.Unauthorized unauthorizedException = new FeignException.Unauthorized(
                    "Unauthorized", request, null, Map.of()
            );

            given(kakaoApiClient.getUserInfo("Bearer " + accessToken)).willThrow(unauthorizedException);

            // when & then
            assertThatThrownBy(() -> kakaoOAuthAdapter.getUserInfo(accessToken))
                    .isInstanceOf(AuthenticationException.class)
                    .satisfies(ex -> assertThat(((AuthenticationException) ex).getErrorCode())
                            .isEqualTo(ErrorCode.KAKAO_AUTH_FAILED));
        }

        @Test
        @DisplayName("실패: RetryableException(타임아웃) 발생 시 EXTERNAL_API_TIMEOUT 예외를 던진다")
        void fail_whenRetryableException() {
            // given
            String accessToken = "token";
            Request request = Request.create(
                    Request.HttpMethod.GET, "https://kapi.kakao.com/v2/user/me",
                    Map.of(), null, Charset.defaultCharset(), null
            );
            RetryableException timeoutException = new RetryableException(
                    408, "Connection timeout", Request.HttpMethod.GET,
                    new Date(), request
            );

            given(kakaoApiClient.getUserInfo("Bearer " + accessToken)).willThrow(timeoutException);

            // when & then
            assertThatThrownBy(() -> kakaoOAuthAdapter.getUserInfo(accessToken))
                    .isInstanceOf(ExternalApiException.class)
                    .satisfies(ex -> assertThat(((ExternalApiException) ex).getErrorCode())
                            .isEqualTo(ErrorCode.EXTERNAL_API_TIMEOUT));
        }

        @Test
        @DisplayName("실패: 일반 FeignException(5xx 등) 발생 시 EXTERNAL_API_ERROR 예외를 던진다")
        void fail_whenGeneralFeignException() {
            // given
            String accessToken = "token";
            Request request = Request.create(
                    Request.HttpMethod.GET, "https://kapi.kakao.com/v2/user/me",
                    Map.of(), null, Charset.defaultCharset(), null
            );
            FeignException serverError = new FeignException.InternalServerError(
                    "Internal Server Error", request, null, Map.of()
            );

            given(kakaoApiClient.getUserInfo("Bearer " + accessToken)).willThrow(serverError);

            // when & then
            assertThatThrownBy(() -> kakaoOAuthAdapter.getUserInfo(accessToken))
                    .isInstanceOf(ExternalApiException.class)
                    .satisfies(ex -> assertThat(((ExternalApiException) ex).getErrorCode())
                            .isEqualTo(ErrorCode.EXTERNAL_API_ERROR));
        }
    }

    // --- 테스트 픽스처 헬퍼 ---

    /**
     * KakaoUserResponse, KakaoAccount, Profile을 ReflectionTestUtils로 생성한다.
     * KakaoUserResponse와 내부 클래스들은 @NoArgsConstructor만 제공하므로 private 필드에 직접 값을 주입한다.
     * 전화번호는 KakaoOAuthAdapter에서 "010-0000-0000"으로 고정되므로 파라미터에서 제외한다.
     */
    private KakaoUserResponse createResponse(Long id, String email, String nickname) {
        KakaoUserResponse.Profile profile = new KakaoUserResponse.Profile();
        ReflectionTestUtils.setField(profile, "nickname", nickname);

        KakaoUserResponse.KakaoAccount account = new KakaoUserResponse.KakaoAccount();
        ReflectionTestUtils.setField(account, "email", email);
        ReflectionTestUtils.setField(account, "profile", profile);

        KakaoUserResponse response = new KakaoUserResponse();
        ReflectionTestUtils.setField(response, "id", id);
        ReflectionTestUtils.setField(response, "kakaoAccount", account);

        return response;
    }

    /**
     * profile이 null인 KakaoAccount를 갖는 응답을 생성한다.
     * KakaoOAuthAdapter가 account.getProfile() == null 케이스를 처리하는지 검증할 때 사용한다.
     */
    private KakaoUserResponse createResponseWithNullProfile(Long id, String email) {
        KakaoUserResponse.KakaoAccount account = new KakaoUserResponse.KakaoAccount();
        ReflectionTestUtils.setField(account, "email", email);
        // profile을 설정하지 않으면 null

        KakaoUserResponse response = new KakaoUserResponse();
        ReflectionTestUtils.setField(response, "id", id);
        ReflectionTestUtils.setField(response, "kakaoAccount", account);

        return response;
    }
}
