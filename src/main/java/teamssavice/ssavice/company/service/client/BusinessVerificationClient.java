package teamssavice.ssavice.company.service.client;

import teamssavice.ssavice.company.service.dto.CompanyCommand;
import teamssavice.ssavice.company.service.dto.CompanyModel;

public interface BusinessVerificationClient {


    // 사업자 등록 정보를 입력받아 진위 여부를 확인
    CompanyModel.Validate validate(CompanyCommand.Validate command);
}
