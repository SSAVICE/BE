package teamssavice.ssavice.address;

import lombok.Builder;

import java.math.BigDecimal;

public class AddressModel {

    @Builder
    public record RegionSummary(
            String gugun,
            String region,
            BigDecimal latitude,
            BigDecimal longitude
    ) {
    }

    @Builder
    public record RegionDetail(
            String gugun,
            String region,
            BigDecimal latitude,
            BigDecimal longitude,
            String postCode,
            String address,
            String detailAddress
    ) {
    }
}
