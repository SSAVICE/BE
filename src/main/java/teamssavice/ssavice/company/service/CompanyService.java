package teamssavice.ssavice.company.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.address.Address;
import teamssavice.ssavice.address.service.AddressService;
import teamssavice.ssavice.address.service.dto.AddressCommand;
import teamssavice.ssavice.auth.Token;
import teamssavice.ssavice.auth.constants.Role;
import teamssavice.ssavice.auth.service.TokenService;
import teamssavice.ssavice.company.entity.Company;
import teamssavice.ssavice.company.infrastructure.repository.CompanyRepository;
import teamssavice.ssavice.company.service.dto.CompanyCommand;
import teamssavice.ssavice.company.service.dto.CompanyModel;
import teamssavice.ssavice.global.constants.ErrorCode;
import teamssavice.ssavice.global.exception.ConflictException;
import teamssavice.ssavice.user.entity.Users;
import teamssavice.ssavice.user.service.UserService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompanyService {
    private final TokenService tokenService;
    private final UserService userService;
    private final CompanyRepository companyRepository;
    private final AddressService addressService;

    @Transactional
    public CompanyModel.Login login(String kakaoToken) {
        // 토큰 검증
        String email = "company@test.com";

        // user 저장 및 중복 체크
        Users user = userService.findByEmail(email)
                .orElseGet(() -> userService.save(email));

        // 업체가 있으면 업체 토큰, 없으면 유저 토큰
        Optional<Company> optionalCompany = companyRepository.findByUser(user);
        if(optionalCompany.isEmpty()) {
            Token token = tokenService.issueToken(user.getId(), Role.USER);
            return CompanyModel.Login.from(token, Role.USER);
        }

        Company company = optionalCompany.get();
        Token token = tokenService.issueToken(company.getId(), Role.COMPANY);
        return CompanyModel.Login.from(token, Role.COMPANY);
    }

    public CompanyModel.Login register(CompanyCommand.Create command) {
        Users user = userService.findById(command.userId());
        if(companyRepository.existsByUser(user)) {
            throw new ConflictException(ErrorCode.COMPANY_ALREADY_EXISTS);
        }
        Address address = addressService.save(AddressCommand.Create.from(command));
        Company company = save(command, user, address);
        Token token = tokenService.issueToken(company.getId(), Role.COMPANY);
        return CompanyModel.Login.from(token, Role.COMPANY);
    }

    private Company save(CompanyCommand.Create command, Users user, Address address) {
        Company company = Company.builder()
                .companyName(command.companyName())
                .ownerName(command.ownerName())
                .phoneNumber(command.phoneNumber())
                .businessNumber(command.businessNumber())
                .description(command.description())
                .depositor(command.depositor())
                .accountNumber(command.accountNumber())
                .detail(command.detail())
                .address(address)
                .user(user)
                .build();
        return companyRepository.save(company);
    }
}
