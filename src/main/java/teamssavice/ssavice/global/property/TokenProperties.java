package teamssavice.ssavice.global.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("token")
public record TokenProperties(
        String secretKey,
        String hashSecretKey,
        Long accessValidityInMilliseconds,
        Long refreshValidityInMilliseconds
) {

}
