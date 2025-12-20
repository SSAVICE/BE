package teamssavice.ssavice.address;

import lombok.Builder;
import teamssavice.ssavice.company.service.dto.CompanyCommand;

import java.math.BigDecimal;

public class AddressCommand {

    @Builder
    public record Update(
            BigDecimal latitude,
            BigDecimal longitude,
            String postCode,
            String address,
            String detailAddress
    ) {
        public static AddressCommand.Update from(CompanyCommand.Update command) {
            return AddressCommand.Update.builder()
                    .latitude(command.latitude())
                    .longitude(command.longitude())
                    .postCode(command.postCode())
                    .address(command.address())
                    .detailAddress(command.detailAddress())
                    .build();
        }
    }
}
