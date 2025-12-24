package teamssavice.ssavice.fixture;

import teamssavice.ssavice.address.Address;
import teamssavice.ssavice.company.entity.Company;
import teamssavice.ssavice.user.entity.Users;

public class CompanyFixture {

    public static Company company(Users user, Address address) {
        return Company.builder()
                .companyName("name")
                .ownerName("owner")
                .phoneNumber("010-8765-4321")
                .businessNumber("company-business")
                .depositor("depositor")
                .accountNumber("account-number")
                .address(address)
                .user(user)
                .build();
    }
}
