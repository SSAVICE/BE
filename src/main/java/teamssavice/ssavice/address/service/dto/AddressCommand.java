package teamssavice.ssavice.address.service.dto;

import lombok.Builder;
import teamssavice.ssavice.company.service.dto.CompanyCommand;

import java.math.BigDecimal;

public class AddressCommand {

    @Builder
    public record Create(
            BigDecimal latitude,
            BigDecimal longitude,
            String postCode,
            String address,
            String detailAddress
    ) {
        public static AddressCommand.Create from(CompanyCommand.Create command) {
            return AddressCommand.Create.builder()
                    .latitude(command.latitude())
                    .longitude(command.longitude())
                    .postCode(command.postCode())
                    .address(command.address())
                    .detailAddress(command.detailAddress())
                    .build();
        }
    }
}
