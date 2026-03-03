package teamssavice.ssavice.oauth.infrastructure.kakao.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import teamssavice.ssavice.oauth.infrastructure.kakao.dto.KakaoUserResponse;

@FeignClient(name = "kakaoApiClient", url = "${external.kakao.user-info-url}")
public interface KakaoApiClient {

    @GetMapping
    KakaoUserResponse getUserInfo(@RequestHeader("Authorization") String bearerToken);
}
