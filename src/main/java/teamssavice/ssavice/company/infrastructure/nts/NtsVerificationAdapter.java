package teamssavice.ssavice.company.infrastructure.nts;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import teamssavice.ssavice.company.infrastructure.nts.client.NtsApiClient;
import teamssavice.ssavice.company.infrastructure.nts.dto.NtsValidationRequest;
import teamssavice.ssavice.company.infrastructure.nts.dto.NtsValidationResponse;
import teamssavice.ssavice.company.service.client.BusinessVerificationClient;

@Component
@RequiredArgsConstructor
public class NtsVerificationAdapter implements BusinessVerificationClient {

    private final NtsApiClient ntsApiClient;

    // nts(국세청) 진위검증 API 관련 서비스 키
    @Value("${external.nts.service-key}")
    private String serviceKey;

    @Override
    public boolean verify(String businessNumber, String openDate, String ownerName) {

        NtsValidationRequest request = NtsValidationRequest.of(businessNumber, openDate, ownerName);

        try {

            NtsValidationResponse response = ntsApiClient.validate(serviceKey, request);

            return isValidResponse

        }
    }
}
