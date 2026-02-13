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
import org.springframework.data.domain.Slice;
import org.springframework.test.annotation.DirtiesContext;
import teamssavice.ssavice.company.entity.Company;
import teamssavice.ssavice.company.infrastructure.repository.CompanyRepository;
import teamssavice.ssavice.address.Address;
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
import teamssavice.ssavice.user.entity.Users;
import teamssavice.ssavice.user.infrastructure.repository.UserRepository;

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

    @Test
    @DisplayName("GeoHash 기반 근처 서비스 검색 - 반경 내 아이템만 반환")
    void findNearbyByGeoHashes_filters_by_radius() {
        // given
        tem.persist(this.user);
        tem.persist(this.company);

        // 서울시청 좌표 (중심점)
        BigDecimal centerLat = new BigDecimal("37.5665");
        BigDecimal centerLon = new BigDecimal("126.9780");

        // 매우 가까운 아이템 (같은 위치, 0m)
        Address sameAddress = createAddressWithCoordinates(centerLat, centerLon);
        ServiceItem sameItem = createRecruitingServiceItem("같은 위치 서비스", sameAddress);
        tem.persist(sameItem);

        // 먼 아이템 (~2.6km 거리, 반경 밖)
        Address farAddress = createAddressWithCoordinates(
                new BigDecimal("37.5900"), new BigDecimal("126.9780"));
        ServiceItem farItem = createRecruitingServiceItem("먼 서비스", farAddress);
        tem.persist(farItem);

        tem.flush();

        int radiusMeters = 1000;
        int precision = GeoHashUtil.getPrecisionForRadius(radiusMeters);
        String centerHash = GeoHashUtil.encode(centerLat, centerLon, precision);
        List<String> geoHashes = GeoHashUtil.getNeighbors(centerHash);
        int size = 10;

        // when
        Slice<ServiceItem> result = serviceItemRepository.findNearbyByGeoHashes(
                centerLat, centerLon, centerLat, centerLon, radiusMeters, geoHashes, size);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("같은 위치 서비스");
        assertThat(result.hasNext()).isFalse();
    }

    @Test
    @DisplayName("GeoHash 기반 근처 서비스 검색 - 거리 순으로 정렬")
    void findNearbyByGeoHashes_sorts_by_distance() {
        // given
        tem.persist(this.user);
        tem.persist(this.company);

        BigDecimal centerLat = new BigDecimal("37.5665");
        BigDecimal centerLon = new BigDecimal("126.9780");

        // 가장 가까운 아이템 (~0m)
        Address nearestAddress = createAddressWithCoordinates(centerLat, centerLon);
        ServiceItem nearestItem = createRecruitingServiceItem("가장 가까운 서비스", nearestAddress);
        tem.persist(nearestItem);

        // 중간 거리 아이템 (조금만 이동, ~100m)
        Address middleAddress = createAddressWithCoordinates(
                new BigDecimal("37.5675"), new BigDecimal("126.9785"));
        ServiceItem middleItem = createRecruitingServiceItem("중간 거리 서비스", middleAddress);
        tem.persist(middleItem);

        // 좀 더 먼 아이템 (~200m)
        Address farAddress = createAddressWithCoordinates(
                new BigDecimal("37.5685"), new BigDecimal("126.9790"));
        ServiceItem farItem = createRecruitingServiceItem("먼 서비스", farAddress);
        tem.persist(farItem);

        tem.flush();

        int radiusMeters = 5000; // 넉넉한 반경 사용
        int precision = GeoHashUtil.getPrecisionForRadius(radiusMeters);
        String centerHash = GeoHashUtil.encode(centerLat, centerLon, precision);
        List<String> geoHashes = GeoHashUtil.getNeighbors(centerHash);
        int size = 10;

        // when
        Slice<ServiceItem> result = serviceItemRepository.findNearbyByGeoHashes(
                centerLat, centerLon, centerLat, centerLon, radiusMeters, geoHashes, size);

        // then
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("가장 가까운 서비스");
        assertThat(result.getContent().get(1).getTitle()).isEqualTo("중간 거리 서비스");
        assertThat(result.getContent().get(2).getTitle()).isEqualTo("먼 서비스");
    }

    @Test
    @DisplayName("GeoHash 기반 근처 서비스 검색 - RECRUITING 상태만 반환")
    void findNearbyByGeoHashes_filters_recruiting_only() {
        // given
        tem.persist(this.user);
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
                centerLat, centerLon, centerLat, centerLon, radiusMeters, geoHashes, size);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("모집 중");
        assertThat(result.getContent().get(0).getStatus()).isEqualTo(ServiceStatus.RECRUITING);
    }

    @Test
    @DisplayName("GeoHash 기반 근처 서비스 검색 - cursor 기반 페이징 동작 확인")
    void findNearbyByGeoHashes_pagination_works() {
        // given
        tem.persist(this.user);
        tem.persist(this.company);

        BigDecimal centerLat = new BigDecimal("37.5665");
        BigDecimal centerLon = new BigDecimal("126.9780");

        // 5개의 근처 아이템 생성 (각각 다른 Address 객체 사용)
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

        // when
        Slice<ServiceItem> slice1 = serviceItemRepository.findNearbyByGeoHashes(
                centerLat, centerLon, centerLat, centerLon, radiusMeters, geoHashes, 3);
        Slice<ServiceItem> slice2 = serviceItemRepository.findNearbyByGeoHashes(
                centerLat, centerLon, centerLat, centerLon, radiusMeters, geoHashes, 10);

        // then
        // 첫 번째 요청: size=3이면 최대 3개 반환, hasNext는 true (5개 중 3개만 가져감)
        assertThat(slice1.getContent()).hasSize(3);
        assertThat(slice1.hasNext()).isTrue();

        // 두 번째 요청: size=10이면 5개 모두 반환, hasNext는 false
        assertThat(slice2.getContent()).hasSize(5);
        assertThat(slice2.hasNext()).isFalse();
    }

    @Test
    @DisplayName("GeoHash 기반 근처 서비스 검색 - 결과가 없을 때 빈 Slice 반환")
    void findNearbyByGeoHashes_returns_empty_when_no_results() {
        // given
        tem.persist(this.user);
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
                centerLat, centerLon, centerLat, centerLon, radiusMeters, geoHashes, size);

        // then
        assertThat(result.getContent()).isEmpty();
        assertThat(result.hasNext()).isFalse();
    }

    @Test
    @DisplayName("GeoHash 기반 근처 서비스 검색 - fetch join으로 N+1 방지")
    void findNearbyByGeoHashes_prevents_n_plus_1() {
        // given
        tem.persist(this.user);
        tem.persist(this.company);

        BigDecimal centerLat = new BigDecimal("37.5665");
        BigDecimal centerLon = new BigDecimal("126.9780");
        Address address = createAddressWithCoordinates(centerLat, centerLon);

        ServiceItem item = createRecruitingServiceItem("테스트 서비스", address);
        tem.persist(item);
        tem.flush();
        tem.clear(); // 영속성 컨텍스트 초기화

        int radiusMeters = 1000;
        int precision = GeoHashUtil.getPrecisionForRadius(radiusMeters);
        String centerHash = GeoHashUtil.encode(centerLat, centerLon, precision);
        List<String> geoHashes = GeoHashUtil.getNeighbors(centerHash);
        int size = 10;

        // when
        Slice<ServiceItem> result = serviceItemRepository.findNearbyByGeoHashes(
                centerLat, centerLon, centerLat, centerLon, radiusMeters, geoHashes, size);

        // then
        assertThat(result.getContent()).hasSize(1);
        ServiceItem foundItem = result.getContent().get(0);

        // fetch join 되어 있으므로 추가 쿼리 없이 접근 가능
        assertThat(foundItem.getCompany()).isNotNull();
        assertThat(foundItem.getCompany().getCompanyName()).isEqualTo("name");
        assertThat(foundItem.getAddress()).isNotNull();
        assertThat(foundItem.getAddress().getGeoHash()).isNotNull();
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
}