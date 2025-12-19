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
        String companyName,
        String ownerName,
        String phoneNumber,
        String businessNumber,
        String accountNumber,
        String description,
        String detail,
        String postCode,
        String address,
        String detailAddress,
        BigDecimal longitude,
        BigDecimal latitude
    ) {
    }
}
