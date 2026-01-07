package teamssavice.ssavice.company.infrastructure.nts;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import teamssavice.ssavice.company.infrastructure.nts.client.NtsApiClient;
import teamssavice.ssavice.company.infrastructure.nts.dto.NtsValidationRequest;
import teamssavice.ssavice.company.infrastructure.nts.dto.NtsValidationResponse;
import teamssavice.ssavice.company.service.client.BusinessVerificationClient;
import teamssavice.ssavice.company.service.dto.CompanyModel;
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
    public CompanyModel.Validate validate(String businessNumber, String startDate, String ownerName) {

        NtsValidationRequest request = NtsValidationRequest.of(businessNumber, startDate, ownerName);

        try {
            NtsValidationResponse response = ntsApiClient.validateBusiness(serviceKey, request);

            var data = response.getData().get(0);

            return CompanyModel.Validate.builder()
                    .isValid("01".equals(data.getValid()))
                    .statusMessage(data.getValidMessage()) // 실패 케이스에는 확인할 수 없습니다 가 잡힘
                    .build();

        } catch (feign.RetryableException e) {
            throw new ExternalApiException(ErrorCode.EXTERNAL_API_TIMEOUT);
        } catch (FeignException e) {
            throw new ExternalApiException(ErrorCode.EXTERNAL_API_ERROR);

        }
    }
}
