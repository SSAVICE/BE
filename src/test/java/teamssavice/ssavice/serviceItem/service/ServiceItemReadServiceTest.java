package teamssavice.ssavice.serviceItem.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import teamssavice.ssavice.global.util.GeoHashUtil;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;
import teamssavice.ssavice.serviceItem.infrastructure.repository.ServiceItemRepository;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ServiceItemReadServiceTest {

    @InjectMocks
    private ServiceItemReadService serviceItemReadService;

    @Mock
    private ServiceItemRepository serviceItemRepository;

    @Nested
    @DisplayName("findNearbyByGeoHash 메서드")
    class FindNearbyByGeoHash {

        @Test
        @DisplayName("성공: 반경 1km 이하일 때 precision 6을 사용한다")
        void success_radius_1km_uses_precision_6() {
            // given
            BigDecimal latitude = new BigDecimal("37.5665");
            BigDecimal longitude = new BigDecimal("126.9780");
            BigDecimal userLatitude = new BigDecimal("37.5665");
            BigDecimal userLongitude = new BigDecimal("126.9780");
            int radiusMeters = 1000;
            int size = 10;

            Slice<ServiceItem> mockSlice = new SliceImpl<>(List.of(), PageRequest.of(0, size), false);
            given(serviceItemRepository.findNearbyByGeoHashes(
                    eq(latitude), eq(longitude), eq(userLatitude), eq(userLongitude), eq(radiusMeters), anyList(), eq(size), isNull()))
                    .willReturn(mockSlice);

            // when
            Slice<ServiceItem> result = serviceItemReadService.findNearbyByGeoHash(
                    latitude, longitude, userLatitude, userLongitude, radiusMeters, size, null);

            // then
            assertThat(result).isNotNull();
            then(serviceItemRepository).should().findNearbyByGeoHashes(
                    eq(latitude), eq(longitude), eq(userLatitude), eq(userLongitude), eq(radiusMeters),
                    argThat(geoHashes -> geoHashes.size() == 9 && geoHashes.get(0).length() == 6),
                    eq(size), isNull()
            );
        }

        @Test
        @DisplayName("성공: 반경 2km일 때 precision 5를 사용한다")
        void success_radius_2km_uses_precision_5() {
            // given
            BigDecimal latitude = new BigDecimal("37.5665");
            BigDecimal longitude = new BigDecimal("126.9780");
            BigDecimal userLatitude = new BigDecimal("37.5665");
            BigDecimal userLongitude = new BigDecimal("126.9780");
            int radiusMeters = 2000;
            int size = 10;

            Slice<ServiceItem> mockSlice = new SliceImpl<>(List.of(), PageRequest.of(0, size), false);
            given(serviceItemRepository.findNearbyByGeoHashes(
                    eq(latitude), eq(longitude), eq(userLatitude), eq(userLongitude), eq(radiusMeters), anyList(), eq(size), isNull()))
                    .willReturn(mockSlice);

            // when
            Slice<ServiceItem> result = serviceItemReadService.findNearbyByGeoHash(
                    latitude, longitude, userLatitude, userLongitude, radiusMeters, size, null);

            // then
            assertThat(result).isNotNull();
            then(serviceItemRepository).should().findNearbyByGeoHashes(
                    eq(latitude), eq(longitude), eq(userLatitude), eq(userLongitude), eq(radiusMeters),
                    argThat(geoHashes -> geoHashes.size() == 9 && geoHashes.get(0).length() == 5),
                    eq(size), isNull()
            );
        }

        @Test
        @DisplayName("성공: 반경 10km일 때 precision 4를 사용한다")
        void success_radius_10km_uses_precision_4() {
            // given
            BigDecimal latitude = new BigDecimal("37.5665");
            BigDecimal longitude = new BigDecimal("126.9780");
            BigDecimal userLatitude = new BigDecimal("37.5665");
            BigDecimal userLongitude = new BigDecimal("126.9780");
            int radiusMeters = 10000;
            int size = 10;

            Slice<ServiceItem> mockSlice = new SliceImpl<>(List.of(), PageRequest.of(0, size), false);
            given(serviceItemRepository.findNearbyByGeoHashes(
                    eq(latitude), eq(longitude), eq(userLatitude), eq(userLongitude), eq(radiusMeters), anyList(), eq(size), isNull()))
                    .willReturn(mockSlice);

            // when
            Slice<ServiceItem> result = serviceItemReadService.findNearbyByGeoHash(
                    latitude, longitude, userLatitude, userLongitude, radiusMeters, size, null);

            // then
            assertThat(result).isNotNull();
            then(serviceItemRepository).should().findNearbyByGeoHashes(
                    eq(latitude), eq(longitude), eq(userLatitude), eq(userLongitude), eq(radiusMeters),
                    argThat(geoHashes -> geoHashes.size() == 9 && geoHashes.get(0).length() == 4),
                    eq(size), isNull()
            );
        }

        @Test
        @DisplayName("성공: 9개의 인접 셀(중심 + 8방향)을 생성한다")
        void success_generates_9_neighbor_cells() {
            // given
            BigDecimal latitude = new BigDecimal("37.5665");
            BigDecimal longitude = new BigDecimal("126.9780");
            BigDecimal userLatitude = new BigDecimal("37.5665");
            BigDecimal userLongitude = new BigDecimal("126.9780");
            int radiusMeters = 1000;
            int size = 10;

            String expectedCenterHash = GeoHashUtil.encode(latitude, longitude, 6);
            List<String> expectedNeighbors = GeoHashUtil.getNeighbors(expectedCenterHash);

            Slice<ServiceItem> mockSlice = new SliceImpl<>(List.of(), PageRequest.of(0, size), false);
            given(serviceItemRepository.findNearbyByGeoHashes(
                    any(), any(), any(), any(), anyInt(), anyList(), anyInt(), any()))
                    .willReturn(mockSlice);

            // when
            serviceItemReadService.findNearbyByGeoHash(latitude, longitude, userLatitude, userLongitude, radiusMeters, size, null);

            // then
            then(serviceItemRepository).should().findNearbyByGeoHashes(
                    eq(latitude), eq(longitude), eq(userLatitude), eq(userLongitude), eq(radiusMeters),
                    argThat(geoHashes ->
                            geoHashes.size() == 9 &&
                            geoHashes.containsAll(expectedNeighbors)
                    ),
                    eq(size), isNull()
            );
        }

        @Test
        @DisplayName("성공: repository에서 반환한 Slice를 그대로 반환한다")
        void success_returns_page_from_repository() {
            // given
            BigDecimal latitude = new BigDecimal("37.5665");
            BigDecimal longitude = new BigDecimal("126.9780");
            BigDecimal userLatitude = new BigDecimal("37.5665");
            BigDecimal userLongitude = new BigDecimal("126.9780");
            int radiusMeters = 1000;
            int size = 10;

            List<ServiceItem> mockItems = List.of(); // 실제 테스트에서는 mock ServiceItem 생성
            Slice<ServiceItem> mockSlice = new SliceImpl<>(mockItems, PageRequest.of(0, size), false);
            given(serviceItemRepository.findNearbyByGeoHashes(
                    any(), any(), any(), any(), anyInt(), anyList(), anyInt(), any()))
                    .willReturn(mockSlice);

            // when
            Slice<ServiceItem> result = serviceItemReadService.findNearbyByGeoHash(
                    latitude, longitude, userLatitude, userLongitude, radiusMeters, size, null);

            // then
            assertThat(result).isEqualTo(mockSlice);
            assertThat(result.getContent()).isEmpty();
        }

        @Test
        @DisplayName("성공: repository 메서드를 정확히 한 번 호출한다")
        void success_calls_repository_exactly_once() {
            // given
            BigDecimal latitude = new BigDecimal("37.5665");
            BigDecimal longitude = new BigDecimal("126.9780");
            BigDecimal userLatitude = new BigDecimal("37.5665");
            BigDecimal userLongitude = new BigDecimal("126.9780");
            int radiusMeters = 1000;
            int size = 10;

            Slice<ServiceItem> mockSlice = new SliceImpl<>(List.of(), PageRequest.of(0, size), false);
            given(serviceItemRepository.findNearbyByGeoHashes(
                    any(), any(), any(), any(), anyInt(), anyList(), anyInt(), any()))
                    .willReturn(mockSlice);

            // when
            serviceItemReadService.findNearbyByGeoHash(latitude, longitude, userLatitude, userLongitude, radiusMeters, size, null);

            // then
            then(serviceItemRepository).should(times(1))
                    .findNearbyByGeoHashes(any(), any(), any(), any(), anyInt(), anyList(), anyInt(), any());
        }

        @Test
        @DisplayName("성공: 극단적인 좌표(북극)에 대해서도 동작한다")
        void success_works_with_extreme_coordinates() {
            // given
            BigDecimal latitude = new BigDecimal("89.9999");
            BigDecimal longitude = new BigDecimal("0.0");
            BigDecimal userLatitude = new BigDecimal("89.9999");
            BigDecimal userLongitude = new BigDecimal("0.0");
            int radiusMeters = 1000;
            int size = 10;

            Slice<ServiceItem> mockSlice = new SliceImpl<>(List.of(), PageRequest.of(0, size), false);
            given(serviceItemRepository.findNearbyByGeoHashes(
                    any(), any(), any(), any(), anyInt(), anyList(), anyInt(), any()))
                    .willReturn(mockSlice);

            // when
            Slice<ServiceItem> result = serviceItemReadService.findNearbyByGeoHash(
                    latitude, longitude, userLatitude, userLongitude, radiusMeters, size, null);

            // then
            assertThat(result).isNotNull();
            then(serviceItemRepository).should().findNearbyByGeoHashes(
                    eq(latitude), eq(longitude), eq(userLatitude), eq(userLongitude), eq(radiusMeters),
                    argThat(geoHashes -> geoHashes.size() == 9),
                    eq(size), isNull()
            );
        }
    }
}
