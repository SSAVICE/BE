package teamssavice.ssavice.auth.controller.dto;

import lombok.Builder;
import teamssavice.ssavice.auth.service.dto.AuthModel;

public class AuthResponse {

    @Builder
    public record Refresh(
            String accessToken,
            Long expiresIn,
            String refreshToken
    ) {
        public static AuthResponse.Refresh from(AuthModel.Refresh model) {
            return AuthResponse.Refresh.builder()
                    .accessToken(model.accessToken())
                    .expiresIn(model.expiresIn())
                    .refreshToken(model.refreshToken())
                    .build();
        }
    }
}
