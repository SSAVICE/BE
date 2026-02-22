package teamssavice.ssavice.company;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.account.entity.Account;
import teamssavice.ssavice.account.infrastructure.repository.AccountRepository;
import teamssavice.ssavice.address.Address;
import teamssavice.ssavice.company.entity.Company;
import teamssavice.ssavice.company.infrastructure.repository.CompanyRepository;
import teamssavice.ssavice.company.service.CompanyService;
import teamssavice.ssavice.company.service.dto.CompanyCommand;
import teamssavice.ssavice.fixture.AddressFixture;
import teamssavice.ssavice.fixture.CompanyFixture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class CompanyIntegrationTest {

    @Autowired
    CompanyService companyService;
    @Autowired
    CompanyRepository companyRepository;
    @Autowired
    AccountRepository accountRepository;

    private Company company;
    private Account account;
    private Address address;

    @BeforeEach
    public void setUp() {
        this.account = CompanyFixture.account();
        this.address = AddressFixture.address();
        this.company = CompanyFixture.company(account, address);
    }

    @Test
    @Transactional
    @DisplayName("Company 업데이트 테스트")
    public void updateCompanyTest() {
        // given
        Account savedAccount = accountRepository.save(this.account);
        Company company = companyRepository.save(CompanyFixture.company(savedAccount, address));
        CompanyCommand.Update command = CompanyCommand.Update.builder()
                .companyId(company.getId())
                .companyName("newCompanyName")
                .detail("this is new detail")
                .build();

        // when
        companyService.updateCompany(command);
        Company actual = companyRepository.findByCompanyIdFetchJoinAddress(company.getId()).get();
        // then
        assertAll(
                () -> assertThat(actual.getCompanyName()).isEqualTo(command.companyName()),
                () -> assertThat(actual.getDetail()).isEqualTo(command.detail()),
                () -> assertThat(actual.getDescription()).isNull()
        );
    }
}
