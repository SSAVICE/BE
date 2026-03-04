package teamssavice.ssavice.account.controller.dto;

import jakarta.validation.constraints.NotBlank;

public class AccountRequest {

    public record delete(
        @NotBlank String accessToken
    ) {
    }
}
