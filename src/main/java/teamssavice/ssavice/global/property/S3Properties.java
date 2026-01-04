package teamssavice.ssavice.global.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("s3")
public record S3Properties(
        String bucket,
        String region,
        Long putExpirationSecond,
        Long getExpirationMin
) {
}
