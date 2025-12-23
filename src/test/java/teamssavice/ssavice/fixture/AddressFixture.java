package teamssavice.ssavice.fixture;

import teamssavice.ssavice.address.Address;

import java.math.BigDecimal;

public class AddressFixture {

    public static Address address() {
        return Address.builder()
                .latitude(BigDecimal.valueOf(33.333))
                .longitude(BigDecimal.valueOf(33.333))
                .postCode("01234")
                .address("address")
                .detailAddress("detailAdd")
                .build();
    }
}
