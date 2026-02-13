package teamssavice.ssavice.global.util;

import java.math.BigDecimal;
import java.util.List;

public final class GeoHashUtil {

    public static final int EARTH_RADIUS_METERS = 6_371_000;
    private static final String BASE32 = "0123456789bcdefghjkmnpqrstuvwxyz";
    public static final int DEFAULT_PRECISION = 6;
    private static final double MIN_LAT = -90.0;
    private static final double MAX_LAT = 90.0;
    private static final double MIN_LON = -180.0;
    private static final double MAX_LON = 180.0;

    private GeoHashUtil() {
    }

    /**
     * 위경도를 GeoHash 문자열로 인코딩
     * <p>
     * 경도/위도를 교대로 이진 분할하여 5bit씩 Base32 문자를 생성한다.
     * precision 6 = 30bit (경도 15bit + 위도 15bit)
     * <p>
     * 시간복잡도: O(1) - precision * 5 = 30회 고정 반복
     */
    public static String encode(BigDecimal latitude, BigDecimal longitude, int precision) {
        return encode(latitude.doubleValue(), longitude.doubleValue(), precision);
    }

    public static String encode(BigDecimal latitude, BigDecimal longitude) {
        return encode(latitude.doubleValue(), longitude.doubleValue(), DEFAULT_PRECISION);
    }

    private static String encode(double lat, double lon, int precision) {
        double latMin = MIN_LAT, latMax = MAX_LAT;
        double lonMin = MIN_LON, lonMax = MAX_LON;

        StringBuilder geohash = new StringBuilder(precision);
        boolean isLon = true;
        int bit = 0;
        int ch = 0;

        while (geohash.length() < precision) {
            double mid;
            if (isLon) {
                mid = (lonMin + lonMax) / 2;
                if (lon >= mid) {
                    ch |= (1 << (4 - bit));
                    lonMin = mid;
                } else {
                    lonMax = mid;
                }
            } else {
                mid = (latMin + latMax) / 2;
                if (lat >= mid) {
                    ch |= (1 << (4 - bit));
                    latMin = mid;
                } else {
                    latMax = mid;
                }
            }
            isLon = !isLon;

            if (++bit == 5) {
                geohash.append(BASE32.charAt(ch));
                bit = 0;
                ch = 0;
            }
        }

        return geohash.toString();
    }

    /**
     * GeoHash를 위경도 범위로 디코딩
     * <p>
     * 반환: [latMin, latMax, lonMin, lonMax]
     */
    private static double[] decodeBounds(String geohash) {
        double latMin = MIN_LAT, latMax = MAX_LAT;
        double lonMin = MIN_LON, lonMax = MAX_LON;
        boolean isLon = true;

        for (char c : geohash.toCharArray()) {
            int charIndex = BASE32.indexOf(c);
            for (int bit = 4; bit >= 0; bit--) {
                if (isLon) {
                    double mid = (lonMin + lonMax) / 2;
                    if (((charIndex >> bit) & 1) == 1) {
                        lonMin = mid;
                    } else {
                        lonMax = mid;
                    }
                } else {
                    double mid = (latMin + latMax) / 2;
                    if (((charIndex >> bit) & 1) == 1) {
                        latMin = mid;
                    } else {
                        latMax = mid;
                    }
                }
                isLon = !isLon;
            }
        }

        return new double[]{latMin, latMax, lonMin, lonMax};
    }

    /**
     * GeoHash를 위경도 중심점으로 디코딩
     * <p>
     * 반환: [latitude, longitude]
     */
    public static double[] decode(String geohash) {
        double[] bounds = decodeBounds(geohash);
        return new double[]{
            (bounds[0] + bounds[1]) / 2,
            (bounds[2] + bounds[3]) / 2
        };
    }

    /**
     * 특정 방향의 인접 셀 GeoHash 계산
     * <p>
     * 현재 셀의 범위를 디코딩한 뒤, 셀 크기만큼 이동하여
     * 인접 셀의 중심점을 re-encode한다.
     * <p>
     * 시간복잡도: O(1)
     */
    private static String neighbor(String geohash, int latDir, int lonDir) {
        double[] bounds = decodeBounds(geohash);
        double latMin = bounds[0], latMax = bounds[1];
        double lonMin = bounds[2], lonMax = bounds[3];

        double latCenter = (latMin + latMax) / 2 + latDir * (latMax - latMin);
        double lonCenter = (lonMin + lonMax) / 2 + lonDir * (lonMax - lonMin);

        return encode(latCenter, lonCenter, geohash.length());
    }

    /**
     * 중심 셀 포함 9개 셀의 GeoHash 반환 (center + 8방향)
     * <p>
     * 시간복잡도: O(1)
     * 반환 순서: [center, N, S, E, W, NE, NW, SE, SW]
     */
    public static List<String> getNeighbors(String geohash) {
        return List.of(
            geohash,
            neighbor(geohash, 1, 0),   // N
            neighbor(geohash, -1, 0),   // S
            neighbor(geohash, 0, 1),    // E
            neighbor(geohash, 0, -1),   // W
            neighbor(geohash, 1, 1),    // NE
            neighbor(geohash, 1, -1),   // NW
            neighbor(geohash, -1, 1),   // SE
            neighbor(geohash, -1, -1)   // SW
        );
    }

    /**
     * Haversine 공식으로 두 좌표 간 거리 계산 (미터)
     * <p>
     * 지구를 반지름 6,371km인 구로 가정한다.
     * 시간복잡도: O(1)
     */
    public static double calculateDistance(
        BigDecimal lat1, BigDecimal lon1,
        BigDecimal lat2, BigDecimal lon2
    ) {
        double earthRadius = EARTH_RADIUS_METERS;

        double dLat = Math.toRadians(lat2.doubleValue() - lat1.doubleValue());
        double dLon = Math.toRadians(lon2.doubleValue() - lon1.doubleValue());

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
            + Math.cos(Math.toRadians(lat1.doubleValue()))
            * Math.cos(Math.toRadians(lat2.doubleValue()))
            * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return earthRadius * c;
    }

    /**
     * 검색 반경에 적합한 precision 반환
     */
    public static int getPrecisionForRadius(int radiusMeters) {
        if (radiusMeters <= 1000) return 6;
        if (radiusMeters <= 5000) return 5;
        if (radiusMeters <= 20000) return 4;
        return 3;
    }
}
