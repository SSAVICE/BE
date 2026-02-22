package teamssavice.ssavice.fixture;

import teamssavice.ssavice.account.constants.Provider;
import teamssavice.ssavice.account.entity.Account;
import teamssavice.ssavice.address.Address;
import teamssavice.ssavice.auth.constants.Role;
import teamssavice.ssavice.company.entity.Company;

public class CompanyFixture {

    public static Account account() {
        return Account.builder()
                .provider(Provider.KAKAO)
                .providerId(String.valueOf(System.nanoTime()))
                .role(Role.COMPANY)
                .build();
    }

    public static Company company(Account account, Address address) {
        return Company.builder()
                .account(account)
                .companyName("name")
                .ownerName("owner")
                .businessName("business")
                .startDate("2025-01-01")
                .phoneNumber("010-8765-4321")
                .businessNumber("company-business")
                .depositor("depositor")
                .accountNumber("account-number")
                .address(address)
                .build();
    }

    public static Company company(Account account) {
        return Company.builder()
                .account(account)
                .companyName("name")
                .ownerName("owner")
                .businessName("business")
                .startDate("2025-01-01")
                .phoneNumber("010-8765-4321")
                .businessNumber("company-business")
                .depositor("depositor")
                .accountNumber("account-number")
                .address(AddressFixture.address())
                .build();
    }
}
