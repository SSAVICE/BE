package teamssavice.ssavice.user.controller.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

public class UserRequest {

    @Builder
    public record Login(
            @NotNull
            String token
    ) {
    }
}
