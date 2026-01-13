package teamssavice.ssavice.company.infrastructure.nts;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import teamssavice.ssavice.company.infrastructure.nts.client.NtsApiClient;
import teamssavice.ssavice.company.infrastructure.nts.dto.NtsValidationRequest;
import teamssavice.ssavice.company.infrastructure.nts.dto.NtsValidationResponse;
import teamssavice.ssavice.company.service.client.BusinessVerificationClient;
import teamssavice.ssavice.company.service.client.BusinessVerifyRequest;
import teamssavice.ssavice.company.service.client.BusinessVerifyResponse;
import teamssavice.ssavice.global.constants.ErrorCode;
import teamssavice.ssavice.global.exception.ExternalApiException;

@Component
@RequiredArgsConstructor
public class NtsVerificationAdapter implements BusinessVerificationClient {

    private final NtsApiClient ntsApiClient;

    // nts(국세청) 진위검증 API 관련 서비스 키
    @Value("${external.nts.service-key}")
    private String serviceKey;

    @Override
    public BusinessVerifyResponse validate(BusinessVerifyRequest request) {

        NtsValidationRequest ntsRequest = NtsValidationRequest.of(
                request.businessNumber(),
                request.startDate(),
                request.name()
        );

        try {
            NtsValidationResponse response = ntsApiClient.validateBusiness(serviceKey, ntsRequest);

            NtsValidationResponse.BusinessDataResponse data = response.getData().get(0);

            return BusinessVerifyResponse.builder()
                    .isValid(data.isValidSuccess())
                    .build();

        } catch (feign.RetryableException e) {
            throw new ExternalApiException(ErrorCode.EXTERNAL_API_TIMEOUT);
        } catch (FeignException e) {
            throw new ExternalApiException(ErrorCode.EXTERNAL_API_ERROR);

        }
    }
}
