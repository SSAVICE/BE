package teamssavice.ssavice.company.infrastructure.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import teamssavice.ssavice.address.Address;
import teamssavice.ssavice.company.entity.Company;
import teamssavice.ssavice.fixture.AddressFixture;
import teamssavice.ssavice.fixture.CompanyFixture;
import teamssavice.ssavice.fixture.UserFixture;
import teamssavice.ssavice.global.config.QueryDSLConfig;
import teamssavice.ssavice.user.entity.Users;
import teamssavice.ssavice.user.infrastructure.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(QueryDSLConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CompanyRepositoryTest {

    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private UserRepository userRepository;

    private Users user;
    private Address address;
    private Company company;

    @BeforeEach
    void setUp() {
        address = AddressFixture.address();
        user = UserFixture.user();
        company = CompanyFixture.company(user, address);
    }

    @Test
    @DisplayName("Company 저장 테스트")
    void saveCompanyTest() {
        // given
        Users user = this.user;
        userRepository.save(user);
        Company company = this.company;
        Address address = this.address;

        // when
        Company actual = companyRepository.save(company);

        // then
        assertAll(
                () -> assertThat(actual.getCompanyName()).isEqualTo(company.getCompanyName()),
                () -> assertThat(actual.getUser().getEmail()).isEqualTo(company.getUser().getEmail()),
                () -> assertThat(actual.getAddress().getAddress()).isEqualTo(address.getAddress())
        );
    }
}