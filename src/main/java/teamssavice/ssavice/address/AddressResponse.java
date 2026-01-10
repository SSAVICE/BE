package teamssavice.ssavice.address;

import lombok.Builder;

import java.math.BigDecimal;

public class AddressResponse {

    @Builder
    public record RegionSummary(
            String gugun,
            String region,
            BigDecimal latitude,
            BigDecimal longitude
    ) {
        public static AddressResponse.RegionSummary from(AddressModel.RegionSummary model) {
            return RegionSummary.builder()
                    .gugun(model.gugun())
                    .region(model.region())
                    .latitude(model.latitude())
                    .longitude(model.longitude())
                    .build();
        }
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
        public static AddressResponse.RegionDetail from(AddressModel.RegionDetail model) {
            return RegionDetail.builder()
                    .gugun(model.gugun())
                    .region(model.region())
                    .latitude(model.latitude())
                    .longitude(model.longitude())
                    .postCode(model.postCode())
                    .address(model.address())
                    .detailAddress(model.detailAddress())
                    .build();
        }
    }
}
