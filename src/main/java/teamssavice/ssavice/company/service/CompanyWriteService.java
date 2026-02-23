package teamssavice.ssavice.company.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.account.constants.Provider;
import teamssavice.ssavice.account.entity.Account;
import teamssavice.ssavice.account.infrastructure.repository.AccountRepository;
import teamssavice.ssavice.address.Address;
import teamssavice.ssavice.address.AddressCommand;
import teamssavice.ssavice.auth.constants.Role;
import teamssavice.ssavice.company.entity.Company;
import teamssavice.ssavice.company.infrastructure.repository.CompanyRepository;
import teamssavice.ssavice.company.service.dto.CompanyCommand;
import teamssavice.ssavice.global.constants.ErrorCode;
import teamssavice.ssavice.global.exception.EntityNotFoundException;
import teamssavice.ssavice.global.util.GeoHashUtil;
import teamssavice.ssavice.oauth.service.client.OAuthUserInfo;

@Service
@RequiredArgsConstructor
public class CompanyWriteService {

    private final CompanyRepository companyRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public Account findOrCreateAccount(OAuthUserInfo oAuthUserInfo, Provider provider) {
        return accountRepository.findByProviderIdAndProviderAndRole(oAuthUserInfo.providerId(), provider, Role.COMPANY)
            .orElseGet(() -> accountRepository.save(Account.builder()
                .provider(provider)
                .providerId(oAuthUserInfo.providerId())
                .role(Role.COMPANY)
                .build()));
    }

    @Transactional
    public Company save(CompanyCommand.Create command, Account account, AddressCommand.RegionInfo addressCommand) {
        Address address = Address.builder()
            .gugun(addressCommand.gugun())
            .gugunCode(addressCommand.gugunCode())
            .region(addressCommand.region())
            .regionCode(addressCommand.regionCode())
            .latitude(command.latitude())
            .longitude(command.longitude())
            .postCode(command.postCode())
            .address(command.address())
            .detailAddress(command.detailAddress())
            .geoHash(GeoHashUtil.encode(command.latitude(), command.longitude()))
            .build();

        Company company = Company.builder()
            .account(account)
            .companyName(command.companyName())
            .businessName(command.businessName())
            .startDate(command.startDate())
            .ownerName(command.ownerName())
            .phoneNumber(command.phoneNumber())
            .businessNumber(command.businessNumber())
            .description(command.description())
            .depositor(command.depositor())
            .accountNumber(command.accountNumber())
            .detail(command.detail())
            .address(address)
            .build();

        return companyRepository.save(company);
    }

    public void addRating(Long companyId, Integer score) {
        int updateCount = companyRepository.addRating(companyId, score);
        if (updateCount == 0) {
            throw new EntityNotFoundException(ErrorCode.COMPANY_NOT_FOUND);
        }
    }
}
