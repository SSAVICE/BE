package teamssavice.ssavice.serviceItem.infrastructure.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import teamssavice.ssavice.company.entity.Company;
import teamssavice.ssavice.company.infrastructure.repository.CompanyRepository;
import teamssavice.ssavice.fixture.AddressFixture;
import teamssavice.ssavice.fixture.CompanyFixture;
import teamssavice.ssavice.fixture.ServiceItemFixture;
import teamssavice.ssavice.fixture.UserFixture;
import teamssavice.ssavice.global.config.QueryDSLConfig;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;
import teamssavice.ssavice.user.entity.Users;
import teamssavice.ssavice.user.infrastructure.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(QueryDSLConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ServiceItemRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ServiceItemRepository serviceItemRepository;
    @Autowired
    private CompanyRepository companyRepository;

    private Users user;
    private Company company;
    private final List<ServiceItem> serviceItems = new ArrayList<>();

    @BeforeEach
    void setUp() {
        user = UserFixture.user();
        company = CompanyFixture.company(user, AddressFixture.address());
        for (int i = 0; i < 5; i++) {
            serviceItems.add(ServiceItemFixture.custom("title" + i, LocalDateTime.now().plusDays(i), company, AddressFixture.address()));
        }
    }

    @Test
    @DisplayName("Deadline 늦은 순서대로 5개 조회 테스트")
    void findTop5ByCompanyOrderByDeadlineDescTest() {
        // given
        userRepository.save(this.user);
        Company company = companyRepository.save(this.company);
        List<ServiceItem> serviceItems = serviceItemRepository.saveAll(this.serviceItems);

        // when
        List<ServiceItem> actuals = serviceItemRepository.findTop5ByCompanyOrderByDeadlineDesc(company);

        // then
        for (int i = 0; i < 5; i++) {
            assertThat(actuals.get(i).getTitle()).isEqualTo(serviceItems.get(5 - i - 1).getTitle());
            assertThat(actuals.get(i).getDeadline()).isEqualTo(serviceItems.get(5 - i - 1).getDeadline());
        }
    }
}