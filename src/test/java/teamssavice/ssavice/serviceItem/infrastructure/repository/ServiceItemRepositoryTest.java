package teamssavice.ssavice.serviceItem.infrastructure.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import teamssavice.ssavice.company.entity.Company;
import teamssavice.ssavice.company.infrastructure.repository.CompanyRepository;
import teamssavice.ssavice.fixture.AddressFixture;
import teamssavice.ssavice.fixture.CompanyFixture;
import teamssavice.ssavice.fixture.ServiceItemFixture;
import teamssavice.ssavice.fixture.UserFixture;
import teamssavice.ssavice.global.config.QueryDSLConfig;
import teamssavice.ssavice.serviceItem.constants.ServiceStatus;
import teamssavice.ssavice.serviceItem.constants.ServiceStatusFilter;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;
import teamssavice.ssavice.user.entity.Users;
import teamssavice.ssavice.user.infrastructure.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

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
    @Autowired
    private TestEntityManager tem;

    private Users user;
    private Company company;
    private final List<ServiceItem> serviceItems = new ArrayList<>();
    ServiceItem recruitingService;
    ServiceItem succeededService;
    ServiceItem fulledService;
    ServiceItem inUseService;
    ServiceItem completeService;
    ServiceItem failService;
    ServiceItem canceledService;

    @BeforeEach
    void setUp() {
        user = UserFixture.user();
        company = CompanyFixture.company(user, AddressFixture.address());
        for (int i = 0; i < 5; i++) {
            serviceItems.add(ServiceItemFixture.custom("title" + i, LocalDateTime.now().plusDays(i), company, AddressFixture.address()));
        }
        recruitingService = ServiceItemFixture.recruiting(company);
        succeededService = ServiceItemFixture.succeeded(company);
        fulledService = ServiceItemFixture.fulled(company);
        inUseService = ServiceItemFixture.inUse(company);
        completeService = ServiceItemFixture.completed(company);
        failService = ServiceItemFixture.failed(company);
        canceledService = ServiceItemFixture.canceled(company);
    }

    @Test
    @DisplayName("Deadline 늦은 순서대로 5개 조회 테스트")
    void findTop5ByCompanyOrderByDeadlineDescTest() {
        // given
        tem.persist(this.user);
        tem.persist(this.company);
        List<ServiceItem> serviceItems = serviceItemRepository.saveAll(this.serviceItems);

        // when
        List<ServiceItem> actuals = serviceItemRepository.findTop5ByCompanyIdOrderByDeadlineDesc(company.getId(), PageRequest.of(0, 5));

        // then
        for (int i = 0; i < 5; i++) {
            assertThat(actuals.get(i).getTitle()).isEqualTo(serviceItems.get(5 - i - 1).getTitle());
            assertThat(actuals.get(i).getDeadline()).isEqualTo(serviceItems.get(5 - i - 1).getDeadline());
        }
    }

    @Test
    @DisplayName("ServiceStatusFilter가 ALL일 때 findByCompany() 테스트")
    void findByCompanyTestWhenAll() {
        // given
        tem.persist(this.user);
        Company company = this.company;
        tem.persist(company);
        tem.persist(recruitingService);
        tem.persist(succeededService);
        tem.persist(fulledService);
        tem.persist(inUseService);
        tem.persist(completeService);
        tem.persist(failService);
        tem.persist(canceledService);

        Pageable pageable = PageRequest.of(0, 20);

        // when
        Page<ServiceItem> actual = serviceItemRepository.findByCompanyAndStatus(company.getId(), ServiceStatusFilter.ALL, pageable);

        // then
        assertThat(actual.getTotalElements()).isEqualTo(7);
    }

    @Test
    @DisplayName("ServiceStatusFilter가 Recruiting일 때 findByCompany() 테스트")
    void findByCompanyTestWhenRecruiting() {
        // given
        tem.persist(this.user);
        Company company = this.company;
        tem.persist(company);
        tem.persist(recruitingService);
        tem.persist(succeededService);
        tem.persist(fulledService);
        tem.persist(inUseService);
        tem.persist(completeService);
        tem.persist(failService);
        tem.persist(canceledService);

        Pageable pageable = PageRequest.of(0, 20);

        // when
        Page<ServiceItem> actual = serviceItemRepository.findByCompanyAndStatus(company.getId(), ServiceStatusFilter.RECRUITING, pageable);

        // then
        assertThat(actual.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("ServiceStatusFilter가 Succeeded일 때 findByCompany() 테스트")
    void findByCompanyTestWhenSucceeded() {
        // given
        tem.persist(this.user);
        Company company = this.company;
        tem.persist(company);
        tem.persist(recruitingService);
        tem.persist(succeededService);
        tem.persist(fulledService);
        tem.persist(inUseService);
        tem.persist(completeService);
        tem.persist(failService);
        tem.persist(canceledService);

        Pageable pageable = PageRequest.of(0, 20);

        // when
        Page<ServiceItem> actual = serviceItemRepository.findByCompanyAndStatus(company.getId(), ServiceStatusFilter.SUCCEEDED, pageable);

        // then
        assertThat(actual.getTotalElements()).isEqualTo(3);
    }

    @Test
    @DisplayName("ServiceStatusFilter가 Canceled일 때 findByCompany() 테스트")
    void findByCompanyTestWhenCanceled() {
        // given
        tem.persist(this.user);
        Company company = this.company;
        tem.persist(company);
        tem.persist(recruitingService);
        tem.persist(succeededService);
        tem.persist(fulledService);
        tem.persist(inUseService);
        tem.persist(completeService);
        tem.persist(failService);
        tem.persist(canceledService);

        Pageable pageable = PageRequest.of(0, 20);

        // when
        Page<ServiceItem> actual = serviceItemRepository.findByCompanyAndStatus(company.getId(), ServiceStatusFilter.CANCELED, pageable);

        // then
        assertThat(actual.getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("ServiceItem 개수 테스트")
    void countServiceItemsByCompanyTest() {
        // given
        LocalDateTime now = LocalDateTime.now();
        tem.persist(this.user);
        Company company = this.company;
        tem.persist(company);
        tem.persist(recruitingService);
        tem.persist(succeededService);
        tem.persist(fulledService);
        tem.persist(inUseService);
        tem.persist(completeService);
        tem.persist(failService);
        tem.persist(canceledService);

        // when
        Long applyCount = serviceItemRepository.countRecruitingServiceItemsByCompanyId(company.getId(), ServiceStatus.RECRUITING, now);
        Long completedCount = serviceItemRepository.countSucceededServiceItemsByCompanyId(company.getId(), ServiceStatus.SUCCEEDED, now);
        Long totalCount = serviceItemRepository.countAllByCompany_Id(company.getId());

        // then
        assertAll(
            () -> assertThat(applyCount).isEqualTo(1),
            () -> assertThat(completedCount).isEqualTo(3),
            () -> assertThat(totalCount).isEqualTo(7)
        );
    }
}