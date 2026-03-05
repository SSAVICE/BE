package teamssavice.ssavice.oauth.infrastructure.kakao.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import teamssavice.ssavice.oauth.infrastructure.kakao.dto.KakaoUserResponse;

@FeignClient(name = "kakaoApiClient", url = "${external.kakao.api-url}")
public interface KakaoApiClient {

    @GetMapping("/v2/user/me")
    KakaoUserResponse getUserInfo(@RequestHeader("Authorization") String bearerToken);

    @PostMapping(value = "/v1/user/unlink", consumes = "application/x-www-form-urlencoded;charset=utf-8")
    void unlinkUser(
        @RequestHeader("Authorization") String adminKeyHeader,
        @RequestParam("target_id_type") String targetIdType,
        @RequestParam("target_id") String targetId
    );
}
