package teamssavice.ssavice.company.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.auth.Token;
import teamssavice.ssavice.auth.constants.Role;
import teamssavice.ssavice.auth.service.TokenService;
import teamssavice.ssavice.company.entity.Company;
import teamssavice.ssavice.company.service.dto.CompanyCommand;
import teamssavice.ssavice.company.service.dto.CompanyModel;
import teamssavice.ssavice.user.entity.Users;
import teamssavice.ssavice.user.service.UserReadService;
import teamssavice.ssavice.user.service.UserWriteService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompanyService {
    private final TokenService tokenService;
    private final UserReadService userReadService;
    private final UserWriteService userWriteService;
    private final CompanyReadService companyReadService;
    private final CompanyWriteService companyWriteService;

    @Transactional
    public CompanyModel.Login login(String kakaoToken) {
        // 토큰 검증
        String email = "company@test.com";

        // user 저장 및 중복 체크
        Users user = userReadService.findByEmail(email)
                .orElseGet(() -> userWriteService.save(email));

        // 업체가 있으면 업체 토큰, 없으면 유저 토큰
        Optional<Company> optionalCompany = companyReadService.findByUser(user);
        if(optionalCompany.isEmpty()) {
            Token token = tokenService.issueToken(user.getId(), Role.USER);
            return CompanyModel.Login.from(token, false);
        }

        Company company = optionalCompany.get();
        Token token = tokenService.issueToken(company.getId(), Role.COMPANY);
        return CompanyModel.Login.from(token, true);
    }

    public CompanyModel.Login register(CompanyCommand.Create command) {
        Users user = userReadService.findById(command.userId());
        companyReadService.checkUserExists(user);

        Company company = companyWriteService.save(command, user);
        Token token = tokenService.issueToken(company.getId(), Role.COMPANY);
        return CompanyModel.Login.from(token, true);
    }

    @Transactional
    public void updateCompany(CompanyCommand.Update command) {
        Company company = companyReadService.findByCompanyIdFetchJoin(command.companyId());
        company.update(command);
    }
}
