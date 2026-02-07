package teamssavice.ssavice.s3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.test.context.TestPropertySource;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import teamssavice.ssavice.global.property.S3Properties;
import teamssavice.ssavice.imageresource.constants.ImageContentType;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {S3Service.class, S3ServiceRetryTest.TestConfig.class})
@TestPropertySource(properties = {
        "s3.region=ap-northeast-2",
        "s3.bucket=test-bucket",
        "s3.put-expiration-second=120",
        "s3.get-expiration-min=5",
        "image.upload.max-bytes=5242880"
})
class S3ServiceRetryTest {

    private static final String SOURCE_KEY = "temp/test-image.jpg";
    private static final String TARGET_KEY = "permanent/test-image.jpg";
    private static final ImageContentType CONTENT_TYPE = ImageContentType.JPEG;
    @Autowired
    private S3Service s3Service;
    @Autowired
    private S3Presigner s3Presigner;
    @Autowired
    private S3Client s3Client;

    @BeforeEach
    void setUp() {
        // 각 테스트마다 mock을 초기화
        org.mockito.Mockito.reset(s3Client, s3Presigner);

        // deleteObject는 NoSuchKeyException을 무시하므로 기본적으로 성공으로 설정
        when(s3Client.deleteObject(any(DeleteObjectRequest.class)))
                .thenReturn(DeleteObjectResponse.builder().build());
    }

    @Test
    @DisplayName("S3Exception 발생 시 최대 3회 시도한다 (초기 1회 + 재시도 2회)")
    void moveObject_retries_three_times_on_s3_exception() {
        // given
        S3Exception s3Exception = (S3Exception) S3Exception.builder()
                .message("Internal Server Error")
                .statusCode(500)
                .build();

        when(s3Client.copyObject(any(CopyObjectRequest.class)))
                .thenThrow(s3Exception);

        // when & then
        assertThatThrownBy(() -> s3Service.copyObject(SOURCE_KEY, TARGET_KEY, CONTENT_TYPE))
                .isInstanceOf(S3Exception.class)
                .hasMessageContaining("Internal Server Error");

        // 총 3회 시도 검증 (maxAttempts = 3)
        verify(s3Client, times(3)).copyObject(any(CopyObjectRequest.class));
    }

    @Test
    @DisplayName("첫 번째 시도 실패 후 두 번째 시도에서 성공하면 정상 처리된다")
    void moveObject_succeeds_on_second_attempt() {
        // given
        S3Exception s3Exception = (S3Exception) S3Exception.builder()
                .message("Temporary error")
                .statusCode(503)
                .build();

        when(s3Client.copyObject(any(CopyObjectRequest.class)))
                .thenThrow(s3Exception)  // 첫 번째 시도 실패
                .thenReturn(CopyObjectResponse.builder().build());  // 두 번째 시도 성공

        // when
        s3Service.copyObject(SOURCE_KEY, TARGET_KEY, CONTENT_TYPE);

        // then
        // copyObject 2회 호출 (첫 실패, 두 번째 성공)
        verify(s3Client, times(2)).copyObject(any(CopyObjectRequest.class));
    }

    @Test
    @DisplayName("두 번째 시도 실패 후 세 번째 시도에서 성공하면 정상 처리된다")
    void moveObject_succeeds_on_third_attempt() {
        // given
        S3Exception s3Exception = (S3Exception) S3Exception.builder()
                .message("Temporary error")
                .statusCode(503)
                .build();

        when(s3Client.copyObject(any(CopyObjectRequest.class)))
                .thenThrow(s3Exception)  // 첫 번째 시도 실패
                .thenThrow(s3Exception)  // 두 번째 시도 실패
                .thenReturn(CopyObjectResponse.builder().build());  // 세 번째 시도 성공

        // when
        s3Service.copyObject(SOURCE_KEY, TARGET_KEY, CONTENT_TYPE);

        // then
        // copyObject 3회 호출 (두 번 실패, 세 번째 성공)
        verify(s3Client, times(3)).copyObject(any(CopyObjectRequest.class));
    }

    @Test
    @DisplayName("3회 모두 실패하면 S3Exception이 던져진다")
    void moveObject_throws_exception_after_all_retries_fail() {
        // given
        S3Exception s3Exception = (S3Exception) S3Exception.builder()
                .message("Persistent failure")
                .statusCode(500)
                .build();

        when(s3Client.copyObject(any(CopyObjectRequest.class)))
                .thenThrow(s3Exception)  // 첫 번째 시도 실패
                .thenThrow(s3Exception)  // 두 번째 시도 실패
                .thenThrow(s3Exception);  // 세 번째 시도 실패

        // when & then
        assertThatThrownBy(() -> s3Service.copyObject(SOURCE_KEY, TARGET_KEY, CONTENT_TYPE))
                .isInstanceOf(S3Exception.class)
                .hasMessageContaining("Persistent failure");

        // 3회 모두 시도 검증
        verify(s3Client, times(3)).copyObject(any(CopyObjectRequest.class));
    }

    @Test
    @DisplayName("재시도 간격이 exponential backoff로 증가한다 (1초, 2초)")
    void moveObject_uses_exponential_backoff() {
        // given
        S3Exception s3Exception = (S3Exception) S3Exception.builder()
                .message("Temporary error")
                .statusCode(503)
                .build();

        when(s3Client.copyObject(any(CopyObjectRequest.class)))
                .thenThrow(s3Exception);

        // when
        Instant start = Instant.now();
        assertThatThrownBy(() -> s3Service.copyObject(SOURCE_KEY, TARGET_KEY, CONTENT_TYPE))
                .isInstanceOf(S3Exception.class);
        Instant end = Instant.now();

        // then
        // 총 소요 시간은 최소 1초(첫 재시도) + 2초(두 번째 재시도) = 3초 이상
        // CI 환경의 지터를 고려하여 2.9초 이상으로 검증
        Duration elapsed = Duration.between(start, end);
        assertThat(elapsed.toMillis()).isGreaterThanOrEqualTo(2900);

        // 3회 시도 확인
        verify(s3Client, times(3)).copyObject(any(CopyObjectRequest.class));
    }

    @Test
    @DisplayName("S3Exception이 아닌 다른 예외는 재시도하지 않는다")
    void moveObject_does_not_retry_non_s3_exception() {
        // given
        RuntimeException runtimeException = new RuntimeException("Unexpected error");

        when(s3Client.copyObject(any(CopyObjectRequest.class)))
                .thenThrow(runtimeException);

        // when & then
        assertThatThrownBy(() -> s3Service.copyObject(SOURCE_KEY, TARGET_KEY, CONTENT_TYPE))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unexpected error");

        // 재시도하지 않으므로 1회만 호출
        verify(s3Client, times(1)).copyObject(any(CopyObjectRequest.class));
    }

    @Configuration
    @EnableRetry
    @EnableConfigurationProperties(S3Properties.class)
    static class TestConfig {
        @Bean
        public S3Presigner s3Presigner() {
            return mock(S3Presigner.class);
        }

        @Bean
        public S3Client s3Client() {
            return mock(S3Client.class);
        }
    }

}
