package teamssavice.ssavice.company.controller.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

public class CompanyRequest {

    @Builder
    public record Login(
            @NotNull
            String token
    ) {
    }

    @Builder
    public record Refresh(
            @NotNull
            String refreshToken
    ) {
    }
}
