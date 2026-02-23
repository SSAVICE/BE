package teamssavice.ssavice.serviceItem.infrastructure.repository;

import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
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
import org.springframework.data.domain.Slice;
import org.springframework.test.annotation.DirtiesContext;
import teamssavice.ssavice.address.Address;
import teamssavice.ssavice.company.entity.Company;
import teamssavice.ssavice.company.infrastructure.repository.CompanyRepository;
import teamssavice.ssavice.account.entity.Account;
import teamssavice.ssavice.fixture.AddressFixture;
import teamssavice.ssavice.fixture.CompanyFixture;
import teamssavice.ssavice.fixture.ServiceItemFixture;
import teamssavice.ssavice.fixture.UserFixture;
import teamssavice.ssavice.global.config.QueryDSLConfig;
import teamssavice.ssavice.global.util.GeoHashUtil;
import teamssavice.ssavice.serviceItem.constants.ServiceStatus;
import teamssavice.ssavice.serviceItem.constants.ServiceStatusFilter;
import teamssavice.ssavice.serviceItem.entity.Price;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;
import teamssavice.ssavice.serviceItem.service.dto.ServiceItemCommand;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
@Import(QueryDSLConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ServiceItemRepositoryTest {
    private final List<ServiceItem> serviceItems = new ArrayList<>();
    ServiceItem recruitingService;
    ServiceItem succeededService;
    ServiceItem fulledService;
    ServiceItem inUseService;
    ServiceItem completeService;
    ServiceItem failService;
    ServiceItem canceledService;
    @Autowired
    private ServiceItemRepository serviceItemRepository;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private TestEntityManager tem;
    @Autowired
    private EntityManagerFactory emf;
    private Account companyAccount;
    private Company company;

    @BeforeEach
    void setUp() {
        companyAccount = CompanyFixture.account();
        company = CompanyFixture.company(companyAccount, AddressFixture.address());
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
        tem.persist(this.companyAccount);
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
        tem.persist(this.companyAccount);
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
        tem.persist(this.companyAccount);
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
        tem.persist(this.companyAccount);
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
        tem.persist(this.companyAccount);
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
        tem.persist(this.companyAccount);
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

    @Test
    @DisplayName("GeoHash 기반 근처 서비스 검색 - 반경 내 아이템만 반환하고 반경 밖 제외")
    void findNearbyByGeoHashes_filters_by_radius() {
        // given
        tem.persist(this.companyAccount);
        tem.persist(this.company);

        // 서울시청 좌표 (중심점)
        BigDecimal centerLat = new BigDecimal("37.5665");
        BigDecimal centerLon = new BigDecimal("126.9780");

        // 반경 내 아이템 (같은 위치, 0m)
        Address insideAddress = createAddressWithCoordinates(centerLat, centerLon);
        ServiceItem insideItem = createRecruitingServiceItem("반경 내 서비스", insideAddress);
        tem.persist(insideItem);

        // 반경 밖 아이템 (~2.6km 거리)
        Address outsideAddress = createAddressWithCoordinates(
            new BigDecimal("37.5900"), new BigDecimal("126.9780"));
        ServiceItem outsideItem = createRecruitingServiceItem("반경 밖 서비스", outsideAddress);
        tem.persist(outsideItem);

        tem.flush();

        int radiusMeters = 500; // 500m 반경
        int precision = GeoHashUtil.getPrecisionForRadius(radiusMeters);
        String centerHash = GeoHashUtil.encode(centerLat, centerLon, precision);
        List<String> geoHashes = GeoHashUtil.getNeighbors(centerHash);
        int size = 10;

        // when
        Slice<ServiceItem> result = serviceItemRepository.findNearbyByGeoHashes(
            centerLat, centerLon, centerLat, centerLon, radiusMeters, geoHashes, size, null);

        // then - 반경 내 아이템만 반환
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("반경 내 서비스");
        assertThat(result.hasNext()).isFalse();
    }

    @Test
    @DisplayName("GeoHash 기반 근처 서비스 검색 - 서로 다른 거리의 아이템들이 거리순 정렬")
    void findNearbyByGeoHashes_sorts_by_distance() {
        // given
        tem.persist(this.companyAccount);
        tem.persist(this.company);

        BigDecimal centerLat = new BigDecimal("37.5665");
        BigDecimal centerLon = new BigDecimal("126.9780");

        // 가장 가까운 아이템 (~0m)
        Address nearestAddress = createAddressWithCoordinates(centerLat, centerLon);
        ServiceItem nearestItem = createRecruitingServiceItem("가장 가까운", nearestAddress);
        tem.persist(nearestItem);

        // 중간 거리 아이템 (~100m)
        Address middleAddress = createAddressWithCoordinates(
            new BigDecimal("37.5675"), new BigDecimal("126.9785"));
        ServiceItem middleItem = createRecruitingServiceItem("중간 거리", middleAddress);
        tem.persist(middleItem);

        // 먼 아이템 (~200m)
        Address farAddress = createAddressWithCoordinates(
            new BigDecimal("37.5685"), new BigDecimal("126.9790"));
        ServiceItem farItem = createRecruitingServiceItem("먼 거리", farAddress);
        tem.persist(farItem);

        tem.flush();

        int radiusMeters = 5000; // 넉넉한 반경 사용
        int precision = GeoHashUtil.getPrecisionForRadius(radiusMeters);
        String centerHash = GeoHashUtil.encode(centerLat, centerLon, precision);
        List<String> geoHashes = GeoHashUtil.getNeighbors(centerHash);
        int size = 10;

        // when
        Slice<ServiceItem> result = serviceItemRepository.findNearbyByGeoHashes(
            centerLat, centerLon, centerLat, centerLon, radiusMeters, geoHashes, size, null);

        // then - 거리 순으로 정렬됨
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("가장 가까운");
        assertThat(result.getContent().get(1).getTitle()).isEqualTo("중간 거리");
        assertThat(result.getContent().get(2).getTitle()).isEqualTo("먼 거리");
    }

    @Test
    @DisplayName("GeoHash 기반 근처 서비스 검색 - RECRUITING 상태만 반환")
    void findNearbyByGeoHashes_filters_recruiting_only() {
        // given
        tem.persist(this.companyAccount);
        tem.persist(this.company);

        BigDecimal centerLat = new BigDecimal("37.5665");
        BigDecimal centerLon = new BigDecimal("126.9780");

        // RECRUITING 상태
        Address address1 = createAddressWithCoordinates(centerLat, centerLon);
        ServiceItem recruitingItem = createRecruitingServiceItem("모집 중", address1);
        tem.persist(recruitingItem);

        // CANCELED 상태
        Address address2 = createAddressWithCoordinates(centerLat, centerLon);
        ServiceItem canceledItem = ServiceItem.builder()
            .title("취소됨")
            .description("설명")
            .price(Price.of(10000L, 10))
            .minimumMember(10L)
            .maximumMember(20L)
            .startDate(LocalDateTime.now().plusDays(10))
            .endDate(LocalDateTime.now().plusDays(30))
            .deadline(LocalDateTime.now().plusDays(5))
            .category("카테고리")
            .company(company)
            .address(address2)
            .status(ServiceStatus.CANCELED)
            .isDeleted(true)
            .build();
        tem.persist(canceledItem);

        // 마감된 RECRUITING (deadline 지남)
        Address address3 = createAddressWithCoordinates(centerLat, centerLon);
        ServiceItem expiredItem = ServiceItem.builder()
            .title("마감됨")
            .description("설명")
            .price(Price.of(10000L, 10))
            .minimumMember(10L)
            .maximumMember(20L)
            .startDate(LocalDateTime.now().minusDays(20))
            .endDate(LocalDateTime.now().plusDays(10))
            .deadline(LocalDateTime.now().minusDays(1))
            .category("카테고리")
            .company(company)
            .address(address3)
            .status(ServiceStatus.RECRUITING)
            .build();
        tem.persist(expiredItem);

        tem.flush();

        int radiusMeters = 1000;
        int precision = GeoHashUtil.getPrecisionForRadius(radiusMeters);
        String centerHash = GeoHashUtil.encode(centerLat, centerLon, precision);
        List<String> geoHashes = GeoHashUtil.getNeighbors(centerHash);
        int size = 10;

        // when
        Slice<ServiceItem> result = serviceItemRepository.findNearbyByGeoHashes(
            centerLat, centerLon, centerLat, centerLon, radiusMeters, geoHashes, size, null);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("모집 중");
        assertThat(result.getContent().get(0).getStatus()).isEqualTo(ServiceStatus.RECRUITING);
    }

    @Test
    @DisplayName("GeoHash 기반 근처 서비스 검색 - hasNext 정확도 검증 (size보다 많음/같음/적음)")
    void findNearbyByGeoHashes_hasNext_accuracy() {
        // given
        tem.persist(this.companyAccount);
        tem.persist(this.company);

        BigDecimal centerLat = new BigDecimal("37.5665");
        BigDecimal centerLon = new BigDecimal("126.9780");

        // 5개의 아이템 생성
        for (int i = 0; i < 5; i++) {
            Address address = createAddressWithCoordinates(centerLat, centerLon);
            ServiceItem item = createRecruitingServiceItem("서비스 " + i, address);
            tem.persist(item);
        }

        tem.flush();

        int radiusMeters = 1000;
        int precision = GeoHashUtil.getPrecisionForRadius(radiusMeters);
        String centerHash = GeoHashUtil.encode(centerLat, centerLon, precision);
        List<String> geoHashes = GeoHashUtil.getNeighbors(centerHash);

        // when & then - size보다 결과가 많을 때 hasNext=true
        Slice<ServiceItem> moreResults = serviceItemRepository.findNearbyByGeoHashes(
            centerLat, centerLon, centerLat, centerLon, radiusMeters, geoHashes, 3, null);
        assertThat(moreResults.getContent()).hasSize(3);
        assertThat(moreResults.hasNext()).isTrue();

        // when & then - size와 결과가 같을 때 hasNext=false
        Slice<ServiceItem> equalResults = serviceItemRepository.findNearbyByGeoHashes(
            centerLat, centerLon, centerLat, centerLon, radiusMeters, geoHashes, 5, null);
        assertThat(equalResults.getContent()).hasSize(5);
        assertThat(equalResults.hasNext()).isFalse();

        // when & then - size보다 결과가 적을 때 hasNext=false
        Slice<ServiceItem> lessResults = serviceItemRepository.findNearbyByGeoHashes(
            centerLat, centerLon, centerLat, centerLon, radiusMeters, geoHashes, 10, null);
        assertThat(lessResults.getContent()).hasSize(5);
        assertThat(lessResults.hasNext()).isFalse();
    }

    @Test
    @DisplayName("GeoHash 기반 근처 서비스 검색 - 결과가 없을 때 빈 Slice 반환")
    void findNearbyByGeoHashes_returns_empty_when_no_results() {
        // given
        tem.persist(this.companyAccount);
        tem.persist(this.company);

        BigDecimal centerLat = new BigDecimal("37.5665");
        BigDecimal centerLon = new BigDecimal("126.9780");

        // 매우 먼 아이템만 생성 (~26km 거리)
        Address farAddress = createAddressWithCoordinates(
            new BigDecimal("37.8000"), new BigDecimal("126.9780"));
        ServiceItem farItem = createRecruitingServiceItem("매우 먼 서비스", farAddress);
        tem.persist(farItem);

        tem.flush();

        int radiusMeters = 1000;
        int precision = GeoHashUtil.getPrecisionForRadius(radiusMeters);
        String centerHash = GeoHashUtil.encode(centerLat, centerLon, precision);
        List<String> geoHashes = GeoHashUtil.getNeighbors(centerHash);
        int size = 10;

        // when
        Slice<ServiceItem> result = serviceItemRepository.findNearbyByGeoHashes(
            centerLat, centerLon, centerLat, centerLon, radiusMeters, geoHashes, size, null);

        // then
        assertThat(result.getContent()).isEmpty();
        assertThat(result.hasNext()).isFalse();
    }

    @Test
    @DisplayName("GeoHash 기반 근처 서비스 검색 - fetch join으로 N+1 쿼리 방지 (Hibernate Statistics 검증)")
    void findNearbyByGeoHashes_prevents_n_plus_1() {
        // given
        tem.persist(this.companyAccount);
        tem.persist(this.company);

        BigDecimal centerLat = new BigDecimal("37.5665");
        BigDecimal centerLon = new BigDecimal("126.9780");

        // 3개의 아이템 생성
        for (int i = 0; i < 3; i++) {
            Address address = createAddressWithCoordinates(centerLat, centerLon);
            ServiceItem item = createRecruitingServiceItem("서비스 " + i, address);
            tem.persist(item);
        }
        tem.flush();
        tem.clear(); // 영속성 컨텍스트 초기화

        int radiusMeters = 1000;
        int precision = GeoHashUtil.getPrecisionForRadius(radiusMeters);
        String centerHash = GeoHashUtil.encode(centerLat, centerLon, precision);
        List<String> geoHashes = GeoHashUtil.getNeighbors(centerHash);
        int size = 10;

        // Hibernate Statistics 활성화
        SessionFactory sessionFactory = emf.unwrap(SessionFactory.class);
        Statistics statistics = sessionFactory.getStatistics();
        statistics.setStatisticsEnabled(true);
        statistics.clear();

        // when
        Slice<ServiceItem> result = serviceItemRepository.findNearbyByGeoHashes(
            centerLat, centerLon, centerLat, centerLon, radiusMeters, geoHashes, size, null);

        // then - fetch join으로 단일 쿼리만 실행 (lastId가 null이므로 address 조회 없음)
        long queryCount = statistics.getPrepareStatementCount();
        assertThat(queryCount).isEqualTo(1L);

        // 결과 검증
        assertThat(result.getContent()).hasSize(3);
        ServiceItem foundItem = result.getContent().get(0);

        // 추가 쿼리 없이 연관 엔티티 접근 가능 확인
        statistics.clear();
        assertThat(foundItem.getCompany()).isNotNull();
        assertThat(foundItem.getCompany().getCompanyName()).isEqualTo("name");
        assertThat(foundItem.getAddress()).isNotNull();
        assertThat(foundItem.getAddress().getGeoHash()).isNotNull();

        // 연관 엔티티 접근 시 추가 쿼리가 발생하지 않음
        assertThat(statistics.getPrepareStatementCount()).isZero();
    }

    private Address createAddressWithCoordinates(BigDecimal lat, BigDecimal lon) {
        return Address.builder()
            .latitude(lat)
            .longitude(lon)
            .geoHash(GeoHashUtil.encode(lat, lon))
            .postCode("12345")
            .address("서울시 중구")
            .detailAddress("세종대로 110")
            .gugun("중구")
            .gugunCode("11010")
            .region("서울")
            .regionCode("11")
            .build();
    }

    private ServiceItem createRecruitingServiceItem(String title, Address address) {
        return ServiceItem.builder()
            .title(title)
            .description("설명")
            .price(Price.of(10000L, 10))
            .minimumMember(10L)
            .maximumMember(20L)
            .startDate(LocalDateTime.now().plusDays(10))
            .endDate(LocalDateTime.now().plusDays(30))
            .deadline(LocalDateTime.now().plusDays(5))
            .category("카테고리")
            .company(company)
            .address(address)
            .status(ServiceStatus.RECRUITING)
            .build();
    }

    @Test
    @DisplayName("Nearby 검색 - lastId가 null이고 결과가 없을 때 빈 Slice 반환")
    void findNearbyByGeoHashes_with_null_lastId_and_no_results_returns_empty_slice() {
        // given
        tem.persist(this.companyAccount);
        tem.persist(this.company);

        BigDecimal centerLat = new BigDecimal("37.5665");
        BigDecimal centerLon = new BigDecimal("126.9780");

        // 아이템을 생성하지 않음
        tem.flush();

        int radiusMeters = 1000;
        int precision = GeoHashUtil.getPrecisionForRadius(radiusMeters);
        String centerHash = GeoHashUtil.encode(centerLat, centerLon, precision);
        List<String> geoHashes = GeoHashUtil.getNeighbors(centerHash);
        int size = 10;

        // when
        Slice<ServiceItem> result = serviceItemRepository.findNearbyByGeoHashes(
            centerLat, centerLon, centerLat, centerLon, radiusMeters, geoHashes, size, null);

        // then
        assertThat(result.getContent()).isEmpty();
        assertThat(result.hasNext()).isFalse();
    }

    @Test
    @DisplayName("Nearby 검색 - lastId로 두 번째 페이지 조회 시 쿼리 수 검증")
    void findNearbyByGeoHashes_with_lastId_returns_second_page() {
        // given
        tem.persist(this.companyAccount);
        tem.persist(this.company);

        BigDecimal centerLat = new BigDecimal("37.5665");
        BigDecimal centerLon = new BigDecimal("126.9780");

        // 5개의 아이템 생성
        List<ServiceItem> items = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Address address = createAddressWithCoordinates(centerLat, centerLon);
            ServiceItem item = createRecruitingServiceItem("서비스 " + i, address);
            tem.persist(item);
            items.add(item);
        }
        tem.flush();
        tem.clear(); // 영속성 컨텍스트 초기화

        int radiusMeters = 1000;
        int precision = GeoHashUtil.getPrecisionForRadius(radiusMeters);
        String centerHash = GeoHashUtil.encode(centerLat, centerLon, precision);
        List<String> geoHashes = GeoHashUtil.getNeighbors(centerHash);
        int size = 3;

        // when - 첫 페이지
        Slice<ServiceItem> firstPage = serviceItemRepository.findNearbyByGeoHashes(
            centerLat, centerLon, centerLat, centerLon, radiusMeters, geoHashes, size, null);

        // then - 첫 페이지 검증
        assertThat(firstPage.getContent()).hasSize(3);
        assertThat(firstPage.hasNext()).isTrue();

        // Hibernate Statistics 활성화
        SessionFactory sessionFactory = emf.unwrap(SessionFactory.class);
        Statistics statistics = sessionFactory.getStatistics();
        statistics.setStatisticsEnabled(true);
        statistics.clear();

        // when - 두 번째 페이지 (첫 페이지 마지막 아이템의 ID 사용)
        Long lastId = firstPage.getContent().get(2).getId();
        Slice<ServiceItem> secondPage = serviceItemRepository.findNearbyByGeoHashes(
            centerLat, centerLon, centerLat, centerLon, radiusMeters, geoHashes, size, lastId);

        // then - lastId가 있을 때는 쿼리 2개 실행 (address 조회 + 메인 쿼리)
        long queryCount = statistics.getPrepareStatementCount();
        assertThat(queryCount).isEqualTo(2L);

        // 두 번째 페이지 검증
        assertThat(secondPage.getContent()).hasSize(2);
        assertThat(secondPage.hasNext()).isFalse();

        // 첫 페이지의 아이템들이 두 번째 페이지에 없어야 함
        List<Long> firstPageIds = firstPage.getContent().stream().map(ServiceItem::getId).toList();
        secondPage.getContent().forEach(item ->
            assertThat(firstPageIds).doesNotContain(item.getId()));
    }

    @Test
    @DisplayName("Nearby 검색 - lastId로 마지막 페이지 조회 시 hasNext가 false")
    void findNearbyByGeoHashes_with_lastId_on_last_page_has_no_next() {
        // given
        tem.persist(this.companyAccount);
        tem.persist(this.company);

        BigDecimal centerLat = new BigDecimal("37.5665");
        BigDecimal centerLon = new BigDecimal("126.9780");

        // 3개의 아이템 생성
        for (int i = 0; i < 3; i++) {
            Address address = createAddressWithCoordinates(centerLat, centerLon);
            ServiceItem item = createRecruitingServiceItem("서비스 " + i, address);
            tem.persist(item);
        }
        tem.flush();

        int radiusMeters = 1000;
        int precision = GeoHashUtil.getPrecisionForRadius(radiusMeters);
        String centerHash = GeoHashUtil.encode(centerLat, centerLon, precision);
        List<String> geoHashes = GeoHashUtil.getNeighbors(centerHash);
        int size = 2;

        // when - 첫 페이지
        Slice<ServiceItem> firstPage = serviceItemRepository.findNearbyByGeoHashes(
            centerLat, centerLon, centerLat, centerLon, radiusMeters, geoHashes, size, null);

        // then - 첫 페이지에는 다음이 있음
        assertThat(firstPage.hasNext()).isTrue();

        // when - 두 번째 페이지 (마지막 페이지)
        Long lastId = firstPage.getContent().get(1).getId();
        Slice<ServiceItem> lastPage = serviceItemRepository.findNearbyByGeoHashes(
            centerLat, centerLon, centerLat, centerLon, radiusMeters, geoHashes, size, lastId);

        // then - 마지막 페이지는 다음이 없음
        assertThat(lastPage.getContent()).hasSize(1);
        assertThat(lastPage.hasNext()).isFalse();
    }

    @Test
    @DisplayName("Nearby 검색 - lastId가 마지막 아이템의 ID일 때 빈 결과 반환")
    void findNearbyByGeoHashes_with_lastId_as_last_item_returns_empty() {
        // given
        tem.persist(this.companyAccount);
        tem.persist(this.company);

        BigDecimal centerLat = new BigDecimal("37.5665");
        BigDecimal centerLon = new BigDecimal("126.9780");

        // 2개의 아이템 생성
        for (int i = 0; i < 2; i++) {
            Address address = createAddressWithCoordinates(centerLat, centerLon);
            ServiceItem item = createRecruitingServiceItem("서비스 " + i, address);
            tem.persist(item);
        }
        tem.flush();

        int radiusMeters = 1000;
        int precision = GeoHashUtil.getPrecisionForRadius(radiusMeters);
        String centerHash = GeoHashUtil.encode(centerLat, centerLon, precision);
        List<String> geoHashes = GeoHashUtil.getNeighbors(centerHash);
        int size = 10;

        // when - 첫 페이지에서 모든 아이템 조회
        Slice<ServiceItem> firstPage = serviceItemRepository.findNearbyByGeoHashes(
            centerLat, centerLon, centerLat, centerLon, radiusMeters, geoHashes, size, null);

        // then - 마지막 아이템의 ID로 조회하면 빈 결과
        Long lastItemId = firstPage.getContent().get(1).getId();
        Slice<ServiceItem> result = serviceItemRepository.findNearbyByGeoHashes(
            centerLat, centerLon, centerLat, centerLon, radiusMeters, geoHashes, size, lastItemId);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.hasNext()).isFalse();
    }

    @Test
    @DisplayName("Nearby 검색 - 존재하지 않는 lastId일 때 cursorCondition이 null이 되어 전체 결과 반환")
    void findNearbyByGeoHashes_with_non_existent_lastId_returns_all_results() {
        // given
        tem.persist(this.companyAccount);
        tem.persist(this.company);

        BigDecimal centerLat = new BigDecimal("37.5665");
        BigDecimal centerLon = new BigDecimal("126.9780");

        // 3개의 아이템 생성
        for (int i = 0; i < 3; i++) {
            Address address = createAddressWithCoordinates(centerLat, centerLon);
            ServiceItem item = createRecruitingServiceItem("서비스 " + i, address);
            tem.persist(item);
        }
        tem.flush();

        int radiusMeters = 1000;
        int precision = GeoHashUtil.getPrecisionForRadius(radiusMeters);
        String centerHash = GeoHashUtil.encode(centerLat, centerLon, precision);
        List<String> geoHashes = GeoHashUtil.getNeighbors(centerHash);
        int size = 10;

        // when - 존재하지 않는 ID로 조회 (cursorCondition이 null이 됨)
        Long nonExistentId = 999999L;
        Slice<ServiceItem> result = serviceItemRepository.findNearbyByGeoHashes(
            centerLat, centerLon, centerLat, centerLon, radiusMeters, geoHashes, size, nonExistentId);

        // then - cursorCondition이 null이므로 모든 아이템 반환
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.hasNext()).isFalse();
    }

    @Test
    @DisplayName("Nearby 검색 - 같은 거리의 아이템들은 id 오름차순으로 정렬됨")
    void findNearbyByGeoHashes_sorts_same_distance_items_by_id_asc() {
        // given
        tem.persist(this.companyAccount);
        tem.persist(this.company);

        BigDecimal centerLat = new BigDecimal("37.5665");
        BigDecimal centerLon = new BigDecimal("126.9780");

        // 같은 위치에 3개의 아이템 생성 (같은 거리)
        List<ServiceItem> items = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Address address = createAddressWithCoordinates(centerLat, centerLon);
            ServiceItem item = createRecruitingServiceItem("서비스 " + i, address);
            tem.persist(item);
            items.add(item);
        }
        tem.flush();

        int radiusMeters = 1000;
        int precision = GeoHashUtil.getPrecisionForRadius(radiusMeters);
        String centerHash = GeoHashUtil.encode(centerLat, centerLon, precision);
        List<String> geoHashes = GeoHashUtil.getNeighbors(centerHash);
        int size = 10;

        // when
        Slice<ServiceItem> result = serviceItemRepository.findNearbyByGeoHashes(
            centerLat, centerLon, centerLat, centerLon, radiusMeters, geoHashes, size, null);

        // then - id 오름차순 정렬 확인
        assertThat(result.getContent()).hasSize(3);
        for (int i = 0; i < 2; i++) {
            assertThat(result.getContent().get(i).getId())
                .isLessThan(result.getContent().get(i + 1).getId());
        }
    }

    @Test
    @DisplayName("Nearby 검색 - lastId와 같은 거리의 다른 아이템은 lastId보다 큰 id만 반환")
    void findNearbyByGeoHashes_with_same_distance_returns_only_greater_ids() {
        // given
        tem.persist(this.companyAccount);
        tem.persist(this.company);

        BigDecimal centerLat = new BigDecimal("37.5665");
        BigDecimal centerLon = new BigDecimal("126.9780");

        // 같은 위치에 5개의 아이템 생성
        List<ServiceItem> items = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Address address = createAddressWithCoordinates(centerLat, centerLon);
            ServiceItem item = createRecruitingServiceItem("서비스 " + i, address);
            tem.persist(item);
            items.add(item);
        }
        tem.flush();

        int radiusMeters = 1000;
        int precision = GeoHashUtil.getPrecisionForRadius(radiusMeters);
        String centerHash = GeoHashUtil.encode(centerLat, centerLon, precision);
        List<String> geoHashes = GeoHashUtil.getNeighbors(centerHash);
        int size = 2;

        // when - 첫 페이지
        Slice<ServiceItem> firstPage = serviceItemRepository.findNearbyByGeoHashes(
            centerLat, centerLon, centerLat, centerLon, radiusMeters, geoHashes, size, null);

        // then - 첫 페이지 검증
        assertThat(firstPage.getContent()).hasSize(2);
        Long lastId = firstPage.getContent().get(1).getId();

        // when - 두 번째 페이지
        Slice<ServiceItem> secondPage = serviceItemRepository.findNearbyByGeoHashes(
            centerLat, centerLon, centerLat, centerLon, radiusMeters, geoHashes, size, lastId);

        // then - lastId보다 큰 id만 반환됨
        assertThat(secondPage.getContent()).hasSize(2);
        secondPage.getContent().forEach(item ->
            assertThat(item.getId()).isGreaterThan(lastId));
    }

    @Test
    @DisplayName("Nearby 검색 - 복합 시나리오: 서로 다른 거리 + 같은 거리 혼합에서 커서 페이지네이션")
    void findNearbyByGeoHashes_complex_scenario_with_mixed_distances() {
        // given
        tem.persist(this.companyAccount);
        tem.persist(this.company);

        BigDecimal centerLat = new BigDecimal("37.5665");
        BigDecimal centerLon = new BigDecimal("126.9780");

        // 가장 가까운 위치에 2개 (같은 거리)
        Address nearAddress1 = createAddressWithCoordinates(centerLat, centerLon);
        ServiceItem nearItem1 = createRecruitingServiceItem("가까운1", nearAddress1);
        tem.persist(nearItem1);

        Address nearAddress2 = createAddressWithCoordinates(centerLat, centerLon);
        ServiceItem nearItem2 = createRecruitingServiceItem("가까운2", nearAddress2);
        tem.persist(nearItem2);

        // 중간 거리에 2개 (같은 거리)
        Address midAddress1 = createAddressWithCoordinates(
            new BigDecimal("37.5675"), new BigDecimal("126.9785"));
        ServiceItem midItem1 = createRecruitingServiceItem("중간1", midAddress1);
        tem.persist(midItem1);

        Address midAddress2 = createAddressWithCoordinates(
            new BigDecimal("37.5675"), new BigDecimal("126.9785"));
        ServiceItem midItem2 = createRecruitingServiceItem("중간2", midAddress2);
        tem.persist(midItem2);

        // 먼 거리에 1개
        Address farAddress = createAddressWithCoordinates(
            new BigDecimal("37.5685"), new BigDecimal("126.9790"));
        ServiceItem farItem = createRecruitingServiceItem("먼거리", farAddress);
        tem.persist(farItem);

        tem.flush();

        int radiusMeters = 5000;
        int precision = GeoHashUtil.getPrecisionForRadius(radiusMeters);
        String centerHash = GeoHashUtil.encode(centerLat, centerLon, precision);
        List<String> geoHashes = GeoHashUtil.getNeighbors(centerHash);
        int size = 2;

        // when - 첫 페이지 (가까운 2개)
        Slice<ServiceItem> page1 = serviceItemRepository.findNearbyByGeoHashes(
            centerLat, centerLon, centerLat, centerLon, radiusMeters, geoHashes, size, null);

        // then
        assertThat(page1.getContent()).hasSize(2);
        assertThat(page1.hasNext()).isTrue();
        assertThat(page1.getContent().get(0).getTitle()).startsWith("가까운");
        assertThat(page1.getContent().get(1).getTitle()).startsWith("가까운");

        // when - 두 번째 페이지 (중간 2개)
        Long lastId1 = page1.getContent().get(1).getId();
        Slice<ServiceItem> page2 = serviceItemRepository.findNearbyByGeoHashes(
            centerLat, centerLon, centerLat, centerLon, radiusMeters, geoHashes, size, lastId1);

        // then
        assertThat(page2.getContent()).hasSize(2);
        assertThat(page2.hasNext()).isTrue();
        assertThat(page2.getContent().get(0).getTitle()).startsWith("중간");
        assertThat(page2.getContent().get(1).getTitle()).startsWith("중간");

        // when - 세 번째 페이지 (먼 거리 1개)
        Long lastId2 = page2.getContent().get(1).getId();
        Slice<ServiceItem> page3 = serviceItemRepository.findNearbyByGeoHashes(
            centerLat, centerLon, centerLat, centerLon, radiusMeters, geoHashes, size, lastId2);

        // then
        assertThat(page3.getContent()).hasSize(1);
        assertThat(page3.hasNext()).isFalse();
        assertThat(page3.getContent().get(0).getTitle()).isEqualTo("먼거리");
    }

    @Test
    @DisplayName("Nearby 검색 - 사용자 위치와 검색 중심이 다를 때 커서 페이지네이션 동작")
    void findNearbyByGeoHashes_with_different_user_and_search_center() {
        // given
        tem.persist(this.companyAccount);
        tem.persist(this.company);

        // 검색 중심 좌표 (서울시청)
        BigDecimal searchLat = new BigDecimal("37.5665");
        BigDecimal searchLon = new BigDecimal("126.9780");

        // 사용자 위치 (조금 떨어진 곳)
        BigDecimal userLat = new BigDecimal("37.5700");
        BigDecimal userLon = new BigDecimal("126.9850");

        // 검색 중심 근처에 아이템 생성
        Address address1 = createAddressWithCoordinates(searchLat, searchLon);
        ServiceItem item1 = createRecruitingServiceItem("서비스1", address1);
        tem.persist(item1);

        Address address2 = createAddressWithCoordinates(
            new BigDecimal("37.5675"), new BigDecimal("126.9785"));
        ServiceItem item2 = createRecruitingServiceItem("서비스2", address2);
        tem.persist(item2);

        tem.flush();

        int radiusMeters = 5000;
        int precision = GeoHashUtil.getPrecisionForRadius(radiusMeters);
        String centerHash = GeoHashUtil.encode(searchLat, searchLon, precision);
        List<String> geoHashes = GeoHashUtil.getNeighbors(centerHash);
        int size = 1;

        // when - 첫 페이지
        Slice<ServiceItem> page1 = serviceItemRepository.findNearbyByGeoHashes(
            searchLat, searchLon, userLat, userLon, radiusMeters, geoHashes, size, null);

        // then
        assertThat(page1.getContent()).hasSize(1);
        assertThat(page1.hasNext()).isTrue();

        // when - 두 번째 페이지
        Long lastId = page1.getContent().get(0).getId();
        Slice<ServiceItem> page2 = serviceItemRepository.findNearbyByGeoHashes(
            searchLat, searchLon, userLat, userLon, radiusMeters, geoHashes, size, lastId);

        // then - 다음 페이지가 정상적으로 조회됨
        assertThat(page2.getContent()).hasSize(1);
        assertThat(page2.hasNext()).isFalse();
        assertThat(page2.getContent().get(0).getId()).isNotEqualTo(page1.getContent().get(0).getId());
    }

    @Test
    @DisplayName("search_sortBy4_거리순정렬_가까운순서로반환")
    void search_sortBy4_returns_items_in_distance_order() {
        // given
        tem.persist(this.companyAccount);
        tem.persist(this.company);

        BigDecimal userLat = new BigDecimal("37.5665");
        BigDecimal userLon = new BigDecimal("126.9780");

        // 가장 가까운 아이템 (~0m)
        Address nearestAddress = createAddressWithCoordinates(userLat, userLon);
        ServiceItem nearestItem = createRecruitingServiceItem("가장 가까운", nearestAddress);
        tem.persist(nearestItem);

        // 중간 거리 아이템 (~100m)
        Address middleAddress = createAddressWithCoordinates(
            new BigDecimal("37.5675"), new BigDecimal("126.9785"));
        ServiceItem middleItem = createRecruitingServiceItem("중간 거리", middleAddress);
        tem.persist(middleItem);

        // 먼 아이템 (~200m)
        Address farAddress = createAddressWithCoordinates(
            new BigDecimal("37.5685"), new BigDecimal("126.9790"));
        ServiceItem farItem = createRecruitingServiceItem("먼 거리", farAddress);
        tem.persist(farItem);

        tem.flush();

        ServiceItemCommand.Search command = ServiceItemCommand.Search.builder()
            .userLatitude(userLat)
            .userLongitude(userLon)
            .sortBy(4)
            .lastId(null)
            .pageable(PageRequest.of(0, 10))
            .onSale(true)
            .build();

        // when
        Slice<ServiceItem> result = serviceItemRepository.search(command);

        // then - 거리순 정렬 확인
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("가장 가까운");
        assertThat(result.getContent().get(1).getTitle()).isEqualTo("중간 거리");
        assertThat(result.getContent().get(2).getTitle()).isEqualTo("먼 거리");
        assertThat(result.hasNext()).isFalse();
    }

    @Test
    @DisplayName("search_sortBy4_2km반경필터")
    void search_sortBy4_filters_by_2km_radius() {
        // given
        tem.persist(this.companyAccount);
        tem.persist(this.company);

        BigDecimal userLat = new BigDecimal("37.5665");
        BigDecimal userLon = new BigDecimal("126.9780");

        // 2km 반경 내 아이템 (~100m)
        Address insideAddress = createAddressWithCoordinates(
            new BigDecimal("37.5675"), new BigDecimal("126.9785"));
        ServiceItem insideItem = createRecruitingServiceItem("반경 내", insideAddress);
        tem.persist(insideItem);

        // 2km 반경 밖 아이템 (~2.6km)
        Address outsideAddress = createAddressWithCoordinates(
            new BigDecimal("37.5900"), new BigDecimal("126.9780"));
        ServiceItem outsideItem = createRecruitingServiceItem("반경 밖", outsideAddress);
        tem.persist(outsideItem);

        tem.flush();

        ServiceItemCommand.Search command = ServiceItemCommand.Search.builder()
            .userLatitude(userLat)
            .userLongitude(userLon)
            .sortBy(4)
            .lastId(null)
            .pageable(PageRequest.of(0, 10))
            .onSale(true)
            .build();

        // when
        Slice<ServiceItem> result = serviceItemRepository.search(command);

        // then - 2km 반경 내 아이템만 반환
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("반경 내");
    }

    @Test
    @DisplayName("search_sortBy4_커서페이지네이션")
    void search_sortBy4_cursor_pagination_with_lastId() {
        // given
        tem.persist(this.companyAccount);
        tem.persist(this.company);

        BigDecimal userLat = new BigDecimal("37.5665");
        BigDecimal userLon = new BigDecimal("126.9780");

        // 5개의 서로 다른 거리의 아이템 생성
        for (int i = 0; i < 5; i++) {
            BigDecimal lat = new BigDecimal("37.5665").add(new BigDecimal("0.001").multiply(new BigDecimal(i)));
            BigDecimal lon = new BigDecimal("126.9780").add(new BigDecimal("0.001").multiply(new BigDecimal(i)));
            Address address = createAddressWithCoordinates(lat, lon);
            ServiceItem item = createRecruitingServiceItem("서비스 " + i, address);
            tem.persist(item);
        }

        tem.flush();
        tem.clear();

        // Hibernate Statistics 활성화
        SessionFactory sessionFactory = emf.unwrap(SessionFactory.class);
        Statistics statistics = sessionFactory.getStatistics();
        statistics.setStatisticsEnabled(true);
        statistics.clear();

        ServiceItemCommand.Search command1 = ServiceItemCommand.Search.builder()
            .userLatitude(userLat)
            .userLongitude(userLon)
            .sortBy(4)
            .lastId(null)
            .pageable(PageRequest.of(0, 3))
            .onSale(true)
            .build();

        // when - 첫 페이지
        Slice<ServiceItem> firstPage = serviceItemRepository.search(command1);

        // then - lastId가 null일 때 쿼리 1개 (메인 쿼리만)
        long queryCount1 = statistics.getPrepareStatementCount();
        assertThat(queryCount1).isEqualTo(1L);
        assertThat(firstPage.getContent()).hasSize(3);
        assertThat(firstPage.hasNext()).isTrue();

        // Hibernate Statistics 초기화
        statistics.clear();

        ServiceItemCommand.Search command2 = ServiceItemCommand.Search.builder()
            .userLatitude(userLat)
            .userLongitude(userLon)
            .sortBy(4)
            .lastId(firstPage.getContent().get(2).getId())
            .pageable(PageRequest.of(0, 3))
            .onSale(true)
            .build();

        // when - 두 번째 페이지
        Slice<ServiceItem> secondPage = serviceItemRepository.search(command2);

        // then - lastId가 있을 때 쿼리 2개 (address 조회 + 메인 쿼리)
        long queryCount2 = statistics.getPrepareStatementCount();
        assertThat(queryCount2).isEqualTo(2L);
        assertThat(secondPage.getContent()).hasSize(2);
        assertThat(secondPage.hasNext()).isFalse();

        // 첫 페이지의 아이템들이 두 번째 페이지에 없어야 함
        List<Long> firstPageIds = firstPage.getContent().stream().map(ServiceItem::getId).toList();
        secondPage.getContent().forEach(item ->
            assertThat(firstPageIds).doesNotContain(item.getId()));
    }

    @Test
    @DisplayName("search_sortBy4_같은거리_ID순정렬")
    void search_sortBy4_sorts_same_distance_items_by_id_asc() {
        // given
        tem.persist(this.companyAccount);
        tem.persist(this.company);

        BigDecimal userLat = new BigDecimal("37.5665");
        BigDecimal userLon = new BigDecimal("126.9780");

        // 같은 위치에 3개의 아이템 생성
        for (int i = 0; i < 3; i++) {
            Address address = createAddressWithCoordinates(userLat, userLon);
            ServiceItem item = createRecruitingServiceItem("서비스 " + i, address);
            tem.persist(item);
        }

        tem.flush();

        ServiceItemCommand.Search command = ServiceItemCommand.Search.builder()
            .userLatitude(userLat)
            .userLongitude(userLon)
            .sortBy(4)
            .lastId(null)
            .pageable(PageRequest.of(0, 10))
            .onSale(true)
            .build();

        // when
        Slice<ServiceItem> result = serviceItemRepository.search(command);

        // then - id 오름차순 정렬 확인
        assertThat(result.getContent()).hasSize(3);
        for (int i = 0; i < 2; i++) {
            assertThat(result.getContent().get(i).getId())
                .isLessThan(result.getContent().get(i + 1).getId());
        }
    }

    @Test
    @DisplayName("search_sortBy4_검색필터와함께")
    void search_sortBy4_works_with_other_filters() {
        // given
        tem.persist(this.companyAccount);
        tem.persist(this.company);

        BigDecimal userLat = new BigDecimal("37.5665");
        BigDecimal userLon = new BigDecimal("126.9780");

        // 카테고리가 일치하는 아이템
        Address address1 = createAddressWithCoordinates(userLat, userLon);
        ServiceItem matchingItem = ServiceItem.builder()
            .title("검색어포함 서비스")
            .description("설명")
            .price(Price.of(15000L, 10))
            .minimumMember(10L)
            .maximumMember(20L)
            .startDate(LocalDateTime.now().plusDays(10))
            .endDate(LocalDateTime.now().plusDays(30))
            .deadline(LocalDateTime.now().plusDays(5))
            .category("카테고리A")
            .company(company)
            .address(address1)
            .status(ServiceStatus.RECRUITING)
            .build();
        tem.persist(matchingItem);

        // 카테고리가 다른 아이템
        Address address2 = createAddressWithCoordinates(userLat, userLon);
        ServiceItem differentCategoryItem = ServiceItem.builder()
            .title("다른 카테고리")
            .description("설명")
            .price(Price.of(10000L, 10))
            .minimumMember(10L)
            .maximumMember(20L)
            .startDate(LocalDateTime.now().plusDays(10))
            .endDate(LocalDateTime.now().plusDays(30))
            .deadline(LocalDateTime.now().plusDays(5))
            .category("카테고리B")
            .company(company)
            .address(address2)
            .status(ServiceStatus.RECRUITING)
            .build();
        tem.persist(differentCategoryItem);

        // 가격 범위 밖 아이템
        Address address3 = createAddressWithCoordinates(userLat, userLon);
        ServiceItem expensiveItem = ServiceItem.builder()
            .title("비싼 서비스")
            .description("설명")
            .price(Price.of(50000L, 10))
            .minimumMember(10L)
            .maximumMember(20L)
            .startDate(LocalDateTime.now().plusDays(10))
            .endDate(LocalDateTime.now().plusDays(30))
            .deadline(LocalDateTime.now().plusDays(5))
            .category("카테고리A")
            .company(company)
            .address(address3)
            .status(ServiceStatus.RECRUITING)
            .build();
        tem.persist(expensiveItem);

        tem.flush();

        ServiceItemCommand.Search command = ServiceItemCommand.Search.builder()
            .userLatitude(userLat)
            .userLongitude(userLon)
            .category("카테고리A")
            .query("검색어포함")
            .minPrice(10000L)
            .maxPrice(20000L)
            .sortBy(4)
            .lastId(null)
            .pageable(PageRequest.of(0, 10))
            .onSale(true)
            .build();

        // when
        Slice<ServiceItem> result = serviceItemRepository.search(command);

        // then - 모든 필터 조건을 만족하는 아이템만 반환
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("검색어포함 서비스");
        assertThat(result.getContent().get(0).getCategory()).isEqualTo("카테고리A");
    }
}