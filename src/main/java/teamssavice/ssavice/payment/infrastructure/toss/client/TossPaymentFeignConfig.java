package teamssavice.ssavice.payment.infrastructure.toss.client;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

// @Configuration을 의도적으로 제외함.
// @FeignClient의 configuration 속성으로 직접 등록되어 해당 클라이언트에만 적용된다.
// @Configuration을 붙이면 Spring 컨텍스트 전역에 Bean으로 등록되어
// 모든 FeignClient에 Basic 인증 헤더가 주입되는 부작용이 발생한다.
public class TossPaymentFeignConfig {

    @Value("${external.toss.secret-key}")
    private String secretKey;

    @Bean
    public RequestInterceptor tossAuthRequestInterceptor() {
        return requestTemplate -> {
            String credentials = secretKey + ":";
            String encoded = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
            requestTemplate.header("Authorization", "Basic " + encoded);
        };
    }
}
