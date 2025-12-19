package teamssavice.ssavice.company.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.auth.Token;
import teamssavice.ssavice.auth.constants.Role;
import teamssavice.ssavice.auth.service.TokenService;
import teamssavice.ssavice.company.infrastructure.repository.CompanyRepository;
import teamssavice.ssavice.company.service.dto.CompanyModel;
import teamssavice.ssavice.user.entity.Users;
import teamssavice.ssavice.user.service.UserService;

@Service
@RequiredArgsConstructor
public class CompanyService {
    private final TokenService tokenService;
    private final UserService userService;
    private final CompanyRepository companyRepository;

    @Transactional
    public CompanyModel.Login login(String kakaoToken) {
        // 토큰 검증
        String email = "company@test.com";

        // user 저장 및 중복 체크
        Users user = userService.findByEmail(email)
                .orElseGet(() -> userService.save(email));

        // 업체가 있으면 업체 토큰, 없으면 유저 토큰
        if (companyRepository.existsByUserId(user.getId())) {
            Token token = tokenService.issueToken(user.getId(), Role.USER);
            return CompanyModel.Login.from(token, Role.USER);
        }
        Token token = tokenService.issueToken(user.getId(), Role.COMPANY);
        return CompanyModel.Login.from(token, Role.COMPANY);
    }
}
