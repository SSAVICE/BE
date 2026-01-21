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
        public static AddressModel.RegionSummary from(Address address) {
            return RegionSummary.builder()
                    .gugun(address.getGugun())
                    .region(address.getRegion())
                    .latitude(address.getLatitude())
                    .longitude(address.getLongitude())
                    .build();
        }
    }

    @Builder
    public record RegionDetail(
            String gugun,
            String region,
            String gugunCode,
            String regionCode,
            BigDecimal latitude,
            BigDecimal longitude,
            String postCode,
            String address,
            String detailAddress
    ) {
        public static AddressModel.RegionDetail from(Address entity) {
            return RegionDetail.builder()
                    .gugun(entity.getGugun())
                    .region(entity.getRegion())
                    .gugunCode(entity.getGugunCode())
                    .regionCode(entity.getRegionCode())
                    .latitude(entity.getLatitude())
                    .longitude(entity.getLongitude())
                    .postCode(entity.getPostCode())
                    .address(entity.getAddress())
                    .detailAddress(entity.getDetailAddress())
                    .build();
        }
    }
}
