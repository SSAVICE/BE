package teamssavice.ssavice.address;

import lombok.Builder;
import teamssavice.ssavice.company.service.dto.CompanyCommand;
import teamssavice.ssavice.serviceItem.service.dto.ServiceItemCommand;

import java.math.BigDecimal;

public class AddressCommand {

    @Builder
    public record RegionInfo(
            String regionCode,
            String gugunCode,
            String region,
            String gugun,
            BigDecimal latitude,
            BigDecimal longitude,
            String postCode,
            String address,
            String detailAddress
    ) {
        public static RegionInfo from(CompanyCommand.Update command, Region entity) {
            return RegionInfo.builder()
                    .regionCode(entity.getRegionCode())
                    .gugunCode(entity.getGugunCode())
                    .region(entity.getRegion())
                    .gugun(entity.getGugun())
                    .latitude(command.latitude())
                    .longitude(command.longitude())
                    .postCode(command.postCode())
                    .address(command.address())
                    .detailAddress(command.detailAddress())
                    .build();
        }

        public static RegionInfo from(CompanyCommand.Create command, Region entity) {
            return RegionInfo.builder()
                    .regionCode(entity.getRegionCode())
                    .gugunCode(entity.getGugunCode())
                    .region(entity.getRegion())
                    .gugun(entity.getGugun())
                    .latitude(command.latitude())
                    .longitude(command.longitude())
                    .postCode(command.postCode())
                    .address(command.address())
                    .detailAddress(command.detailAddress())
                    .build();
        }

        public static RegionInfo from(ServiceItemCommand.Create command, Region entity) {
            return RegionInfo.builder()
                    .regionCode(entity.getRegionCode())
                    .gugunCode(entity.getGugunCode())
                    .region(entity.getRegion())
                    .gugun(entity.getGugun())
                    .latitude(command.latitude())
                    .longitude(command.longitude())
                    .postCode(command.postCode())
                    .address(command.address())
                    .detailAddress(command.detailAddress())
                    .build();
        }
    }
}
