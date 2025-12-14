package teamssavice.ssavice.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

public class UserRequest {

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
