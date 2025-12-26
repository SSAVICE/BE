package teamssavice.ssavice.auth.controller.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

public class AuthRequest {


    @Builder
    public record Refresh(
            @NotNull
            String refreshToken
    ) {
    }
}
