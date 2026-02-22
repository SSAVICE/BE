package teamssavice.ssavice.company.controller.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import teamssavice.ssavice.account.constants.Provider;
import teamssavice.ssavice.address.AddressRequest;
import teamssavice.ssavice.company.service.dto.CompanyCommand;

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
        @NotNull
        String name,
        @NotNull
        String startDate,
        @NotNull
        String businessNumber,
        @NotNull
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
