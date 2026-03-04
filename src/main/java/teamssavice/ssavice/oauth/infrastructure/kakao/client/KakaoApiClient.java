package teamssavice.ssavice.oauth.infrastructure.kakao.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import teamssavice.ssavice.oauth.infrastructure.kakao.dto.KakaoUserResponse;

@FeignClient(name = "kakaoApiClient", url = "${external.kakao.api-url}")
public interface KakaoApiClient {

    @GetMapping("/v2/user/me")
    KakaoUserResponse getUserInfo(@RequestHeader("Authorization") String bearerToken);

    @PostMapping("/v1/user/unlink")
    void unlinkUser(@RequestHeader("Authorization") String bearerToken);
}
