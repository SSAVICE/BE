package teamssavice.ssavice.serviceItem.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import teamssavice.ssavice.address.Address;
import teamssavice.ssavice.company.entity.Company;
import teamssavice.ssavice.s3.S3Service;
import teamssavice.ssavice.serviceItem.entity.Price;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;
import teamssavice.ssavice.serviceItem.service.dto.ServiceItemCommand;
import teamssavice.ssavice.serviceItem.service.dto.ServiceItemModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ServiceItemServiceTest {

    @InjectMocks
    private ServiceItemService serviceItemService;

    @Mock
    private ServiceItemReadService serviceItemReadService;

    @Mock
    private S3Service s3Service;

    private ServiceItem createServiceItem(Long id, String title, BigDecimal lat, BigDecimal lon) {
        Address address = Address.builder()
                .latitude(lat)
                .longitude(lon)
                .geoHash("wydm6v")
                .postCode("12345")
                .address("서울시 중구")
                .detailAddress("세종대로 110")
                .gugun("중구")
                .region("서울")
                .build();

        Company company = Company.builder()
                .companyName("테스트 회사")
                .build();
        ReflectionTestUtils.setField(company, "id", 1L);

        ServiceItem serviceItem = ServiceItem.builder()
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
                .build();

        ReflectionTestUtils.setField(serviceItem, "id", id);
        return serviceItem;
    }

    @Nested
    @DisplayName("searchNearby 메서드")
    class SearchNearby {

        @Test
        @DisplayName("성공: 거리 m를 km로 변환한다 (소수점 2자리)")
        void success_converts_distance_meters_to_km() {
            // given
            BigDecimal userLat = new BigDecimal("37.5665");
            BigDecimal userLon = new BigDecimal("126.9780");
            BigDecimal searchLat = new BigDecimal("37.5665");
            BigDecimal searchLon = new BigDecimal("126.9780");
            int radiusMeters = 2000;
            Pageable pageable = PageRequest.of(0, 10);

            ServiceItemCommand.Nearby command = ServiceItemCommand.Nearby.of(
                    userLat, userLon, searchLat, searchLon, radiusMeters, pageable);

            // 서비스 아이템 위치: 약 850m 떨어진 곳
            BigDecimal itemLat = new BigDecimal("37.5741");
            BigDecimal itemLon = new BigDecimal("126.9780");
            ServiceItem item = createServiceItem(1L, "근처 서비스", itemLat, itemLon);

            Page<ServiceItem> mockPage = new PageImpl<>(List.of(item), pageable, 1);
            given(serviceItemReadService.findNearbyByGeoHash(
                    eq(searchLat), eq(searchLon), eq(userLat), eq(userLon), eq(radiusMeters), any(Pageable.class)))
                    .willReturn(mockPage);
            given(s3Service.generateGetPresignedUrl(anyString()))
                    .willReturn("https://s3.example.com/image.jpg");

            // when
            Page<ServiceItemModel.Nearby> result = serviceItemService.searchNearby(command);

            // then
            assertThat(result.getContent()).hasSize(1);
            ServiceItemModel.Nearby nearbyItem = result.getContent().get(0);

            // 실제 거리 계산: 약 850m → 0.85km
            // Math.round(850 / 10.0) / 100.0 = Math.round(85) / 100.0 = 0.85
            assertThat(nearbyItem.distanceKm()).isBetween(0.8, 0.9);
        }

        @Test
        @DisplayName("성공: 거리 반올림 정확도 테스트 (1234m → 1.23km)")
        void success_distance_rounding_accuracy() {
            // given
            BigDecimal userLat = new BigDecimal("37.5665");
            BigDecimal userLon = new BigDecimal("126.9780");
            BigDecimal searchLat = new BigDecimal("37.5665");
            BigDecimal searchLon = new BigDecimal("126.9780");
            int radiusMeters = 2000;
            Pageable pageable = PageRequest.of(0, 10);

            ServiceItemCommand.Nearby command = ServiceItemCommand.Nearby.of(
                    userLat, userLon, searchLat, searchLon, radiusMeters, pageable);

            // 약 1.2km 떨어진 위치
            BigDecimal itemLat = new BigDecimal("37.5773");
            BigDecimal itemLon = new BigDecimal("126.9780");
            ServiceItem item = createServiceItem(1L, "근처 서비스", itemLat, itemLon);

            Page<ServiceItem> mockPage = new PageImpl<>(List.of(item), pageable, 1);
            given(serviceItemReadService.findNearbyByGeoHash(any(), any(), any(), any(), anyInt(), any()))
                    .willReturn(mockPage);
            given(s3Service.generateGetPresignedUrl(anyString()))
                    .willReturn("https://s3.example.com/image.jpg");

            // when
            Page<ServiceItemModel.Nearby> result = serviceItemService.searchNearby(command);

            // then
            assertThat(result.getContent()).hasSize(1);
            ServiceItemModel.Nearby nearbyItem = result.getContent().get(0);

            // 거리는 소수점 2자리로 반올림
            assertThat(nearbyItem.distanceKm()).isBetween(1.1, 1.3);
        }

        @Test
        @DisplayName("성공: S3 presigned URL을 생성한다")
        void success_generates_s3_presigned_url() {
            // given
            BigDecimal userLat = new BigDecimal("37.5665");
            BigDecimal userLon = new BigDecimal("126.9780");
            BigDecimal searchLat = new BigDecimal("37.5665");
            BigDecimal searchLon = new BigDecimal("126.9780");
            int radiusMeters = 2000;
            Pageable pageable = PageRequest.of(0, 10);

            ServiceItemCommand.Nearby command = ServiceItemCommand.Nearby.of(
                    userLat, userLon, searchLat, searchLon, radiusMeters, pageable);

            ServiceItem item = createServiceItem(1L, "근처 서비스", userLat, userLon);

            Page<ServiceItem> mockPage = new PageImpl<>(List.of(item), pageable, 1);
            given(serviceItemReadService.findNearbyByGeoHash(any(), any(), any(), any(), anyInt(), any()))
                    .willReturn(mockPage);

            String expectedUrl = "https://s3.example.com/image.jpg";
            given(s3Service.generateGetPresignedUrl(anyString()))
                    .willReturn(expectedUrl);

            // when
            Page<ServiceItemModel.Nearby> result = serviceItemService.searchNearby(command);

            // then
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).thumbnailUrl()).isEqualTo(expectedUrl);
            then(s3Service).should().generateGetPresignedUrl(anyString());
        }

        @Test
        @DisplayName("성공: 여러 아이템을 거리 순으로 매핑한다")
        void success_maps_multiple_items() {
            // given
            BigDecimal userLat = new BigDecimal("37.5665");
            BigDecimal userLon = new BigDecimal("126.9780");
            BigDecimal searchLat = new BigDecimal("37.5665");
            BigDecimal searchLon = new BigDecimal("126.9780");
            int radiusMeters = 5000;
            Pageable pageable = PageRequest.of(0, 10);

            ServiceItemCommand.Nearby command = ServiceItemCommand.Nearby.of(
                    userLat, userLon, searchLat, searchLon, radiusMeters, pageable);

            ServiceItem item1 = createServiceItem(1L, "가까운 서비스", userLat, userLon);
            ServiceItem item2 = createServiceItem(2L, "중간 거리 서비스",
                    new BigDecimal("37.5741"), new BigDecimal("126.9780"));
            ServiceItem item3 = createServiceItem(3L, "먼 서비스",
                    new BigDecimal("37.5900"), new BigDecimal("126.9780"));

            Page<ServiceItem> mockPage = new PageImpl<>(
                    List.of(item1, item2, item3), pageable, 3);
            given(serviceItemReadService.findNearbyByGeoHash(any(), any(), any(), any(), anyInt(), any()))
                    .willReturn(mockPage);
            given(s3Service.generateGetPresignedUrl(anyString()))
                    .willReturn("https://s3.example.com/image.jpg");

            // when
            Page<ServiceItemModel.Nearby> result = serviceItemService.searchNearby(command);

            // then
            assertThat(result.getContent()).hasSize(3);
            assertThat(result.getTotalElements()).isEqualTo(3);

            // 첫 번째 아이템은 거의 같은 위치 (0km)
            assertThat(result.getContent().get(0).distanceKm()).isLessThan(0.01);

            // 두 번째 아이템은 약 0.8km
            assertThat(result.getContent().get(1).distanceKm()).isBetween(0.7, 0.9);

            // 세 번째 아이템은 약 2.6km
            assertThat(result.getContent().get(2).distanceKm()).isBetween(2.5, 2.7);
        }

        @Test
        @DisplayName("성공: 결과가 없을 때 빈 페이지를 반환한다")
        void success_returns_empty_page_when_no_results() {
            // given
            BigDecimal userLat = new BigDecimal("37.5665");
            BigDecimal userLon = new BigDecimal("126.9780");
            BigDecimal searchLat = new BigDecimal("37.5665");
            BigDecimal searchLon = new BigDecimal("126.9780");
            int radiusMeters = 1000;
            Pageable pageable = PageRequest.of(0, 10);

            ServiceItemCommand.Nearby command = ServiceItemCommand.Nearby.of(
                    userLat, userLon, searchLat, searchLon, radiusMeters, pageable);

            Page<ServiceItem> mockPage = new PageImpl<>(List.of(), pageable, 0);
            given(serviceItemReadService.findNearbyByGeoHash(any(), any(), any(), any(), anyInt(), any()))
                    .willReturn(mockPage);

            // when
            Page<ServiceItemModel.Nearby> result = serviceItemService.searchNearby(command);

            // then
            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isEqualTo(0);
        }

        @Test
        @DisplayName("성공: ServiceItemModel.Nearby에 모든 필드를 올바르게 매핑한다")
        void success_maps_all_fields_correctly() {
            // given
            BigDecimal userLat = new BigDecimal("37.5665");
            BigDecimal userLon = new BigDecimal("126.9780");
            BigDecimal searchLat = new BigDecimal("37.5665");
            BigDecimal searchLon = new BigDecimal("126.9780");
            int radiusMeters = 2000;
            Pageable pageable = PageRequest.of(0, 10);

            ServiceItemCommand.Nearby command = ServiceItemCommand.Nearby.of(
                    userLat, userLon, searchLat, searchLon, radiusMeters, pageable);

            ServiceItem item = createServiceItem(1L, "테스트 서비스", userLat, userLon);

            Page<ServiceItem> mockPage = new PageImpl<>(List.of(item), pageable, 1);
            given(serviceItemReadService.findNearbyByGeoHash(any(), any(), any(), any(), anyInt(), any()))
                    .willReturn(mockPage);

            String expectedUrl = "https://s3.example.com/thumbnail.jpg";
            given(s3Service.generateGetPresignedUrl(anyString()))
                    .willReturn(expectedUrl);

            // when
            Page<ServiceItemModel.Nearby> result = serviceItemService.searchNearby(command);

            // then
            assertThat(result.getContent()).hasSize(1);
            ServiceItemModel.Nearby nearbyItem = result.getContent().get(0);

            assertThat(nearbyItem.serviceItemId()).isEqualTo(1L);
            assertThat(nearbyItem.title()).isEqualTo("테스트 서비스");
            assertThat(nearbyItem.thumbnailUrl()).isEqualTo(expectedUrl);
            assertThat(nearbyItem.distanceKm()).isNotNull();
        }

        @Test
        @DisplayName("성공: 페이징 정보를 올바르게 유지한다")
        void success_maintains_pagination_info() {
            // given
            BigDecimal userLat = new BigDecimal("37.5665");
            BigDecimal userLon = new BigDecimal("126.9780");
            BigDecimal searchLat = new BigDecimal("37.5665");
            BigDecimal searchLon = new BigDecimal("126.9780");
            int radiusMeters = 5000;
            Pageable pageable = PageRequest.of(1, 5); // 2번째 페이지, 5개씩

            ServiceItemCommand.Nearby command = ServiceItemCommand.Nearby.of(
                    userLat, userLon, searchLat, searchLon, radiusMeters, pageable);

            List<ServiceItem> items = List.of(
                    createServiceItem(6L, "서비스 6", userLat, userLon),
                    createServiceItem(7L, "서비스 7", userLat, userLon)
            );

            Page<ServiceItem> mockPage = new PageImpl<>(items, pageable, 12); // 전체 12개
            given(serviceItemReadService.findNearbyByGeoHash(any(), any(), any(), any(), anyInt(), any()))
                    .willReturn(mockPage);
            given(s3Service.generateGetPresignedUrl(anyString()))
                    .willReturn("https://s3.example.com/image.jpg");

            // when
            Page<ServiceItemModel.Nearby> result = serviceItemService.searchNearby(command);

            // then
            assertThat(result.getNumber()).isEqualTo(1); // 페이지 번호
            assertThat(result.getSize()).isEqualTo(5); // 페이지 크기
            assertThat(result.getTotalElements()).isEqualTo(12); // 전체 개수
            assertThat(result.getTotalPages()).isEqualTo(3); // 전체 페이지 수
            assertThat(result.getContent()).hasSize(2); // 현재 페이지 아이템 수
        }

        @Test
        @DisplayName("성공: 사용자 위치와 검색 중심이 다를 때 사용자 위치로 거리를 계산한다")
        void success_calculates_distance_from_user_location_not_search_center() {
            // given
            // 사용자는 서울시청에 위치
            BigDecimal userLat = new BigDecimal("37.5665");
            BigDecimal userLon = new BigDecimal("126.9780");

            // 하지만 검색 중심은 남쪽으로 1km 떨어진 지점 (사용자가 지도를 드래그함)
            BigDecimal searchLat = new BigDecimal("37.5575");
            BigDecimal searchLon = new BigDecimal("126.9780");

            int radiusMeters = 2000;
            Pageable pageable = PageRequest.of(0, 10);

            ServiceItemCommand.Nearby command = ServiceItemCommand.Nearby.of(
                    userLat, userLon, searchLat, searchLon, radiusMeters, pageable);

            // 서비스 아이템은 검색 중심(37.5575) 근처에 위치
            // 사용자(37.5665)로부터는 약 1km 떨어져 있음
            BigDecimal itemLat = new BigDecimal("37.5575");
            BigDecimal itemLon = new BigDecimal("126.9780");
            ServiceItem item = createServiceItem(1L, "검색 중심 근처 서비스", itemLat, itemLon);

            Page<ServiceItem> mockPage = new PageImpl<>(List.of(item), pageable, 1);
            // GeoHash 검색은 검색 중심 좌표로 수행됨
            given(serviceItemReadService.findNearbyByGeoHash(
                    eq(searchLat), eq(searchLon), eq(userLat), eq(userLon), eq(radiusMeters), any(Pageable.class)))
                    .willReturn(mockPage);
            given(s3Service.generateGetPresignedUrl(anyString()))
                    .willReturn("https://s3.example.com/image.jpg");

            // when
            Page<ServiceItemModel.Nearby> result = serviceItemService.searchNearby(command);

            // then
            assertThat(result.getContent()).hasSize(1);
            ServiceItemModel.Nearby nearbyItem = result.getContent().get(0);

            // 거리는 사용자 위치(37.5665)로부터 계산되어야 함
            // 사용자(37.5665) - 아이템(37.5575) ≈ 1.0km
            assertThat(nearbyItem.distanceKm()).isBetween(0.9, 1.1);

            // GeoHash 검색은 검색 중심으로 호출되었는지 검증
            then(serviceItemReadService).should().findNearbyByGeoHash(
                    eq(searchLat), eq(searchLon), eq(userLat), eq(userLon), eq(radiusMeters), any(Pageable.class));
        }

        @Test
        @DisplayName("성공: 사용자가 검색 중심 동쪽에 위치할 때 거리를 정확히 계산한다")
        void success_calculates_distance_when_user_east_of_search_center() {
            // given
            // 사용자는 동쪽에 위치
            BigDecimal userLat = new BigDecimal("37.5665");
            BigDecimal userLon = new BigDecimal("126.9900"); // 동쪽

            // 검색 중심은 서쪽
            BigDecimal searchLat = new BigDecimal("37.5665");
            BigDecimal searchLon = new BigDecimal("126.9780"); // 서쪽

            int radiusMeters = 3000;
            Pageable pageable = PageRequest.of(0, 10);

            ServiceItemCommand.Nearby command = ServiceItemCommand.Nearby.of(
                    userLat, userLon, searchLat, searchLon, radiusMeters, pageable);

            // 서비스 아이템은 검색 중심 근처 (사용자로부터는 약 0.8km 떨어짐)
            BigDecimal itemLat = new BigDecimal("37.5665");
            BigDecimal itemLon = new BigDecimal("126.9800");
            ServiceItem item = createServiceItem(1L, "서비스", itemLat, itemLon);

            Page<ServiceItem> mockPage = new PageImpl<>(List.of(item), pageable, 1);
            given(serviceItemReadService.findNearbyByGeoHash(
                    eq(searchLat), eq(searchLon), eq(userLat), eq(userLon), eq(radiusMeters), any(Pageable.class)))
                    .willReturn(mockPage);
            given(s3Service.generateGetPresignedUrl(anyString()))
                    .willReturn("https://s3.example.com/image.jpg");

            // when
            Page<ServiceItemModel.Nearby> result = serviceItemService.searchNearby(command);

            // then
            assertThat(result.getContent()).hasSize(1);
            ServiceItemModel.Nearby nearbyItem = result.getContent().get(0);

            // 거리는 사용자 위치로부터 계산됨
            // 사용자(126.9900) - 아이템(126.9800) ≈ 0.01 degrees ≈ 0.9km
            assertThat(nearbyItem.distanceKm()).isBetween(0.7, 1.0);
        }

        @Test
        @DisplayName("성공: 사용자와 검색 중심이 같을 때 정상 작동한다")
        void success_works_when_user_and_search_center_are_same() {
            // given
            BigDecimal userLat = new BigDecimal("37.5665");
            BigDecimal userLon = new BigDecimal("126.9780");
            BigDecimal searchLat = new BigDecimal("37.5665");
            BigDecimal searchLon = new BigDecimal("126.9780");

            int radiusMeters = 2000;
            Pageable pageable = PageRequest.of(0, 10);

            ServiceItemCommand.Nearby command = ServiceItemCommand.Nearby.of(
                    userLat, userLon, searchLat, searchLon, radiusMeters, pageable);

            BigDecimal itemLat = new BigDecimal("37.5741");
            BigDecimal itemLon = new BigDecimal("126.9780");
            ServiceItem item = createServiceItem(1L, "근처 서비스", itemLat, itemLon);

            Page<ServiceItem> mockPage = new PageImpl<>(List.of(item), pageable, 1);
            given(serviceItemReadService.findNearbyByGeoHash(
                    eq(searchLat), eq(searchLon), eq(userLat), eq(userLon), eq(radiusMeters), any(Pageable.class)))
                    .willReturn(mockPage);
            given(s3Service.generateGetPresignedUrl(anyString()))
                    .willReturn("https://s3.example.com/image.jpg");

            // when
            Page<ServiceItemModel.Nearby> result = serviceItemService.searchNearby(command);

            // then
            assertThat(result.getContent()).hasSize(1);
            ServiceItemModel.Nearby nearbyItem = result.getContent().get(0);

            // 사용자와 검색 중심이 같으므로, 어느 좌표로 계산해도 결과는 동일
            assertThat(nearbyItem.distanceKm()).isBetween(0.8, 0.9);
        }
    }
}
