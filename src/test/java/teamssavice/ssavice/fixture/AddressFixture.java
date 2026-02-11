package teamssavice.ssavice.fixture;

import teamssavice.ssavice.address.Address;
import teamssavice.ssavice.global.util.GeoHashUtil;

import java.math.BigDecimal;

public class AddressFixture {

    public static Address address() {
        BigDecimal lat = BigDecimal.valueOf(33.333);
        BigDecimal lon = BigDecimal.valueOf(33.333);
        return Address.builder()
                .latitude(lat)
                .longitude(lon)
                .geoHash(GeoHashUtil.encode(lat, lon))
                .postCode("01234")
                .address("address")
                .detailAddress("detailAdd")
                .build();
    }
}
