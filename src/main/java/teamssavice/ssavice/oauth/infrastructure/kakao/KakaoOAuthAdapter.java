package teamssavice.ssavice.oauth.infrastructure.kakao;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import teamssavice.ssavice.global.constants.ErrorCode;
import teamssavice.ssavice.global.exception.AuthenticationException;
import teamssavice.ssavice.global.exception.ExternalApiException;
import teamssavice.ssavice.oauth.infrastructure.kakao.client.KakaoApiClient;
import teamssavice.ssavice.oauth.infrastructure.kakao.dto.KakaoUserResponse;
import teamssavice.ssavice.oauth.service.client.OAuthClient;
import teamssavice.ssavice.oauth.service.client.OAuthUserInfo;
import teamssavice.ssavice.user.constants.Provider;

@Component
@RequiredArgsConstructor
public class KakaoOAuthAdapter implements OAuthClient {

    private final KakaoApiClient kakaoApiClient;

    @Override
    public Provider getProvider() {
        return Provider.KAKAO;
    }

    @Override
    public OAuthUserInfo getUserInfo(String accessToken) {
        try {
            KakaoUserResponse response = kakaoApiClient.getUserInfo("Bearer " + accessToken);

            if (response.getId() == null) {
                throw new AuthenticationException(ErrorCode.KAKAO_AUTH_FAILED);
            }

            KakaoUserResponse.KakaoAccount account = response.getKakaoAccount();
            if (account == null
                    || account.getEmail() == null
                    || account.getName() == null
                    || account.getPhoneNumber() == null) {
                throw new AuthenticationException(ErrorCode.KAKAO_INFO_NOT_PROVIDED);
            }

            return OAuthUserInfo.builder()
                .providerId(String.valueOf(response.getId()))
                .email(account.getEmail())
                .name(account.getName())
                .phoneNumber(account.getPhoneNumber())
                .build();

        } catch (FeignException.Unauthorized e) {
            throw new AuthenticationException(ErrorCode.KAKAO_AUTH_FAILED);
        } catch (feign.RetryableException e) {
            throw new ExternalApiException(ErrorCode.EXTERNAL_API_TIMEOUT);
        } catch (FeignException e) {
            throw new ExternalApiException(ErrorCode.EXTERNAL_API_ERROR);
        }
    }
}
