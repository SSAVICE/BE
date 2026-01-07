package teamssavice.ssavice.user.controller.dto;

import jakarta.validation.constraints.NotBlank;
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
    public record Modify(
        @NotBlank
        String name,
        @NotBlank
        String email,
        @NotBlank
        String phoneNumber
    ) {

    }
}
