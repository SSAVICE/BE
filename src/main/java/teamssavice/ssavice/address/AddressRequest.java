package teamssavice.ssavice.address;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;

public class AddressRequest {

    @Builder
    public record Region(
            @NotNull
            String regionCode,
            @NotNull
            String postCode,
            @NotNull
            String address,
            @NotNull
            String detailAddress,
            @NotNull
            BigDecimal longitude,
            @NotNull
            BigDecimal latitude
    ) {
        public AddressCommand.Update toCommand(Long userId) {
            return AddressCommand.Update.builder()
                    .userId(userId)
                    .regionCode(regionCode)
                    .postCode(postCode)
                    .address(address)
                    .detailAddress(detailAddress)
                    .longitude(longitude)
                    .latitude(latitude)
                    .build();
        }
    }
}
