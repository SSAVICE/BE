package teamssavice.ssavice.global.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("opensearch")
public record OpenSearchProperties(
        String host,
        int port,
        String scheme,
        String username,
        String password
) {
}