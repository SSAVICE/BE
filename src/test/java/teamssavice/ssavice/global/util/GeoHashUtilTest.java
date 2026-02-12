package teamssavice.ssavice.global.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GeoHashUtilTest {

    @Test
    @DisplayName("서울시청 좌표 인코딩 - 알려진 GeoHash와 일치")
    void encode_seoul_city_hall() {
        BigDecimal lat = new BigDecimal("37.5665");
        BigDecimal lon = new BigDecimal("126.9780");

        String geohash = GeoHashUtil.encode(lat, lon, 6);

        //서울시청 geohash wydm9q
        assertThat(geohash).hasSize(6);
        assertThat(geohash).startsWith("wydm9q");
    }

    @Test
    @DisplayName("인코딩 후 디코딩하면 원본 좌표에 근접")
    void encode_then_decode_returns_approximate_coordinates() {
        BigDecimal lat = new BigDecimal("37.5665");
        BigDecimal lon = new BigDecimal("126.9780");

        String geohash = GeoHashUtil.encode(lat, lon, 6);
        double[] decoded = GeoHashUtil.decode(geohash);

        // precision 6 → 약 ±0.001도 오차
        assertThat(decoded[0]).isCloseTo(37.5665, org.assertj.core.data.Offset.offset(0.01));
        assertThat(decoded[1]).isCloseTo(126.9780, org.assertj.core.data.Offset.offset(0.01));
    }

    @Test
    @DisplayName("인접 9개 셀 반환 - center 포함")
    void getNeighbors_returns_9_cells() {
        String geohash = GeoHashUtil.encode(
            new BigDecimal("37.5665"),
            new BigDecimal("126.9780"),
            6
        );

        List<String> neighbors = GeoHashUtil.getNeighbors(geohash);

        assertThat(neighbors).hasSize(9);
        assertThat(neighbors.get(0)).isEqualTo(geohash); // center
        // 모든 이웃이 동일한 길이
        neighbors.forEach(n -> assertThat(n).hasSize(6));
        // 중복 없음
        assertThat(neighbors).doesNotHaveDuplicates();
    }

    @Test
    @DisplayName("Haversine 거리 계산 - 서울시청↔강남역 약 8km")
    void calculateDistance_seoul_gangnam() {
        // 서울시청
        BigDecimal lat1 = new BigDecimal("37.5665");
        BigDecimal lon1 = new BigDecimal("126.9780");
        // 강남역
        BigDecimal lat2 = new BigDecimal("37.4979");
        BigDecimal lon2 = new BigDecimal("127.0276");

        double distance = GeoHashUtil.calculateDistance(lat1, lon1, lat2, lon2);
        // 약 약 8.8 km (8.793 km)
        assertThat(distance).isBetween(8750.0, 8880.0);
    }

    @Test
    @DisplayName("같은 좌표의 거리는 0")
    void calculateDistance_same_point_is_zero() {
        BigDecimal lat = new BigDecimal("37.5665");
        BigDecimal lon = new BigDecimal("126.9780");

        double distance = GeoHashUtil.calculateDistance(lat, lon, lat, lon);

        assertThat(distance).isEqualTo(0.0);
    }

    @Test
    @DisplayName("반경별 precision 선택")
    void getPrecisionForRadius() {
        assertThat(GeoHashUtil.getPrecisionForRadius(500)).isEqualTo(6);
        assertThat(GeoHashUtil.getPrecisionForRadius(1000)).isEqualTo(6);
        assertThat(GeoHashUtil.getPrecisionForRadius(2000)).isEqualTo(5);
        assertThat(GeoHashUtil.getPrecisionForRadius(5000)).isEqualTo(5);
        assertThat(GeoHashUtil.getPrecisionForRadius(10000)).isEqualTo(4);
        assertThat(GeoHashUtil.getPrecisionForRadius(25000)).isEqualTo(3);
    }

    @Test
    @DisplayName("precision별 셀 크기 - 정밀도가 높을수록 셀이 작아진다")
    void precision_cell_size_decreases() {
        BigDecimal lat = new BigDecimal("37.5665");
        BigDecimal lon = new BigDecimal("126.9780");

        String hash6 = GeoHashUtil.encode(lat, lon, 6);
        String hash5 = GeoHashUtil.encode(lat, lon, 5);
        String hash4 = GeoHashUtil.encode(lat, lon, 4);

        // 낮은 precision은 높은 precision의 prefix
        assertThat(hash6).startsWith(hash5);
        assertThat(hash5).startsWith(hash4);
    }

    @Test
    @DisplayName("극단적 좌표 인코딩 - 북극/남극/경도 경계")
    void encode_extreme_coordinates() {
        // 북극
        String northPole = GeoHashUtil.encode(new BigDecimal("90.0"), new BigDecimal("0.0"), 6);
        assertThat(northPole).hasSize(6);

        // 남극
        String southPole = GeoHashUtil.encode(new BigDecimal("-90.0"), new BigDecimal("0.0"), 6);
        assertThat(southPole).hasSize(6);

        // 동경 180도
        String east180 = GeoHashUtil.encode(new BigDecimal("0.0"), new BigDecimal("180.0"), 6);
        assertThat(east180).hasSize(6);

        // 서경 180도
        String west180 = GeoHashUtil.encode(new BigDecimal("0.0"), new BigDecimal("-180.0"), 6);
        assertThat(west180).hasSize(6);
    }

    @Test
    @DisplayName("인접 셀 중복 검사 - 9개 모두 고유해야 한다")
    void neighbors_should_be_unique() {
        String center = GeoHashUtil.encode(
            new BigDecimal("37.5665"),
            new BigDecimal("126.9780"),
            5
        );

        List<String> neighbors = GeoHashUtil.getNeighbors(center);

        assertThat(neighbors).hasSize(9);
        assertThat(neighbors).doesNotHaveDuplicates();
    }

    @Test
    @DisplayName("Haversine 거리 계산 - 적도 vs 고위도 정확도")
    void calculateDistance_accuracy_at_different_latitudes() {
        // 적도 근처 (위도 0도)
        BigDecimal equatorLat1 = new BigDecimal("0.0");
        BigDecimal equatorLon1 = new BigDecimal("0.0");
        BigDecimal equatorLat2 = new BigDecimal("0.0");
        BigDecimal equatorLon2 = new BigDecimal("0.01"); // 경도 0.01도 차이

        double equatorDistance = GeoHashUtil.calculateDistance(
            equatorLat1, equatorLon1, equatorLat2, equatorLon2);

        // 약 1.1km (적도에서 경도 1도 ≈ 111km)
        assertThat(equatorDistance).isBetween(1000.0, 1200.0);

        // 고위도 (서울 위도 37도)
        BigDecimal seoulLat1 = new BigDecimal("37.5665");
        BigDecimal seoulLon1 = new BigDecimal("126.9780");
        BigDecimal seoulLat2 = new BigDecimal("37.5665");
        BigDecimal seoulLon2 = new BigDecimal("126.9880"); // 경도 0.01도 차이

        double seoulDistance = GeoHashUtil.calculateDistance(
            seoulLat1, seoulLon1, seoulLat2, seoulLon2);

        // 고위도에서는 경도 거리가 더 짧음 (약 0.8km)
        assertThat(seoulDistance).isBetween(700.0, 900.0);
        assertThat(seoulDistance).isLessThan(equatorDistance);
    }
}
