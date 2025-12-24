package teamssavice.ssavice.company.controller.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;

public class CompanyRequest {

    @Builder
    public record Login(
            @NotNull
            String token
    ) {
    }

    @Builder
    public record Create(
            @NotNull
            String companyName,
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
            String postCode,
            String address,
            String detailAddress,
            BigDecimal longitude,
            BigDecimal latitude
    ) {
    }
}
