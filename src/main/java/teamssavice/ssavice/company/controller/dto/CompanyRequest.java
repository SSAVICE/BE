package teamssavice.ssavice.company.controller.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import teamssavice.ssavice.address.AddressRequest;
import teamssavice.ssavice.company.service.dto.CompanyCommand;
import teamssavice.ssavice.user.constants.Provider;

public class CompanyRequest {

    @Builder
    public record Login(
        @NotNull
        String token,
        @NotNull
        Provider provider
    ) {

    }

    @Builder
    public record Create(
        @NotNull
        String verifyToken,
        @NotNull
        String companyName,
        @NotNull
        String businessName,
        @NotNull
        String startDate,
        @NotNull
        String ownerName,
        @NotNull
        String phoneNumber,
        @NotNull
        String businessNumber,

        String description,
        @NotNull
        String depositor,
        @NotNull
        String accountNumber,

        String detail,
        @NotNull
        AddressRequest.Region region
    ) {

    }

    @Builder
    public record Update(
        String companyName,
        String ownerName,
        String phoneNumber,
        String description,
        String depositor,
        String accountNumber,
        String detail,
        AddressRequest.Region region
    ) {

    }

    @Builder
    public record Validate(
        String name,
        String startDate,
        String businessNumber,
        String businessName
    ) {

        public CompanyCommand.Validate toCommand() {
            return CompanyCommand.Validate.builder()
                .businessNumber(businessNumber)
                .startDate(startDate)
                .name(name)
                .businessName(businessName)
                .build();
        }
    }
}
