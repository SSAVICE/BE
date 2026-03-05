package teamssavice.ssavice.global.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("external.kakao")
public record KakaoProperties(
        String apiUrl,
        String adminKey
) {
}
