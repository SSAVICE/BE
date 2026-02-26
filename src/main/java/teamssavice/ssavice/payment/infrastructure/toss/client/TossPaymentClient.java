package teamssavice.ssavice.payment.infrastructure.toss.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import teamssavice.ssavice.payment.infrastructure.toss.dto.TossConfirmRequest;
import teamssavice.ssavice.payment.infrastructure.toss.dto.TossConfirmResponse;

@FeignClient(
        name = "tossPaymentClient",
        url = "${external.toss.url}",
        configuration = TossPaymentFeignConfig.class
)
public interface TossPaymentClient {

    @PostMapping("/confirm")
    TossConfirmResponse confirm(@RequestBody TossConfirmRequest request);
}
