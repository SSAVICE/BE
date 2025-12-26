package teamssavice.ssavice.auth.service.dto;

import lombok.Builder;
import teamssavice.ssavice.auth.Token;

public class AuthModel {

    @Builder
    public record Refresh(
            String accessToken,
            Long expiresIn,
            String refreshToken
    ) {
        public static Refresh from(Token token) {
            return Refresh.builder()
                    .accessToken(token.accessToken())
                    .expiresIn(token.expiresIn())
                    .refreshToken(token.refreshToken())
                    .build();
        }
    }
}
