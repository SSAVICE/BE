package teamssavice.ssavice.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import teamssavice.ssavice.global.property.S3Properties;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class S3Config {

    private final S3Properties s3Properties;

    @Bean
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
                .region(Region.of(s3Properties.region()))
                .build();
    }

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(s3Properties.region()))
                .httpClient(
                        ApacheHttpClient.builder()
                                .connectionTimeout(Duration.ofMillis(300)) //TCP 연결을 맺기까지 기다리는 최대 시간
                                .socketTimeout(Duration.ofSeconds(3)) //연결은 되었지만, 응답 데이터를 못 받고 있는 상태에서 기다리는 시간
                                .build()
                ).build();
    }
}