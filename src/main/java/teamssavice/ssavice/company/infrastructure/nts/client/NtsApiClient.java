package teamssavice.ssavice.company.infrastructure.nts.client;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import teamssavice.ssavice.company.infrastructure.nts.dto.NtsValidationRequest;

@FeignClient(name = "ntsApiClient", url = "https://api.odcloud.kr/api/nts-businessman/v1")
public interface NtsApiClient {

    @PostMapping("/validate")
    NtsApiResponse validate(
            @RequestParam("serviceKey") String serviceKey,
            @RequestBody NtsValidationRequest request
    );
}
