package teamssavice.ssavice.company.infrastructure.nts.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import teamssavice.ssavice.company.infrastructure.nts.dto.NtsValidationRequest;
import teamssavice.ssavice.company.infrastructure.nts.dto.NtsValidationResponse;


@FeignClient(name = "ntsApiClient", url = "${external.nts.url}")
public interface NtsApiClient {

    @PostMapping("/validate")
    NtsValidationResponse validateBusiness(
            @RequestParam("serviceKey") String serviceKey,
            @RequestBody NtsValidationRequest request
    );
}
