package teamssavice.ssavice.company;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.address.Address;
import teamssavice.ssavice.company.entity.Company;
import teamssavice.ssavice.company.infrastructure.repository.CompanyRepository;
import teamssavice.ssavice.company.service.CompanyService;
import teamssavice.ssavice.company.service.dto.CompanyCommand;
import teamssavice.ssavice.fixture.AddressFixture;
import teamssavice.ssavice.fixture.CompanyFixture;
import teamssavice.ssavice.fixture.UserFixture;
import teamssavice.ssavice.user.entity.Users;
import teamssavice.ssavice.user.infrastructure.repository.UserRepository;
import teamssavice.ssavice.user.service.UserService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class CompanyIntegrationTest {

    @Autowired
    CompanyService companyService;
    @Autowired
    CompanyRepository companyRepository;
    @Autowired
    UserRepository userRepository;

    private Company company;
    private Users user;
    private Address address;
    @Autowired
    private UserService userService;

    @BeforeEach
    public void setUp() {
        this.user = UserFixture.user();
        this.address = AddressFixture.address();
        this.company = CompanyFixture.company(user, address);
    }

    @Test
    @DisplayName("Company 업데이트 테스트")
    public void updateCompanyTest() {
        // given
        userRepository.save(this.user);
        Company company = companyRepository.save(this.company);
        CompanyCommand.Update command = CompanyCommand.Update.builder()
                .companyId(company.getId())
                .companyName("newCompanyName")
                .detail("this is new detail")
                .address("this is new address")
                .build();

        // when
        companyService.updateCompany(command);
        Company actual = companyRepository.findByCompanyIdFetchJoin(company.getId()).get();
        // then
        assertAll(
                () -> assertThat(actual.getCompanyName()).isEqualTo(command.companyName()),
                () -> assertThat(actual.getDetail()).isEqualTo(command.detail()),
                () -> assertThat(actual.getDescription()).isNull(),
                () -> assertThat(actual.getAddress().getAddress()).isEqualTo(command.address())
        );
    }
}
