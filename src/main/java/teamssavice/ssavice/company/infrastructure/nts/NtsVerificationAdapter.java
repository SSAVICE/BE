package teamssavice.ssavice.company.infrastructure.nts;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import teamssavice.ssavice.company.infrastructure.dto.CompanyInfraCommand;
import teamssavice.ssavice.company.infrastructure.dto.CompanyInfraModel;
import teamssavice.ssavice.company.infrastructure.nts.client.NtsApiClient;
import teamssavice.ssavice.company.infrastructure.nts.dto.NtsValidationRequest;
import teamssavice.ssavice.company.infrastructure.nts.dto.NtsValidationResponse;
import teamssavice.ssavice.company.service.client.BusinessVerificationClient;
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
    public CompanyInfraModel.Validate validate(CompanyInfraCommand.Validate command) {

        NtsValidationRequest request = NtsValidationRequest.of(command.businessNumber(),
            command.startDate(), command.name());

        try {
            NtsValidationResponse response = ntsApiClient.validateBusiness(serviceKey, request);
            var data = response.getData().get(0);

            return CompanyInfraModel.Validate.builder()
                .isValid("01".equals(data.getValid()))
                .build();
        } catch (feign.RetryableException e) {
            throw new ExternalApiException(ErrorCode.EXTERNAL_API_TIMEOUT);
        } catch (FeignException e) {
            throw new ExternalApiException(ErrorCode.EXTERNAL_API_ERROR);
        }
    }
}
