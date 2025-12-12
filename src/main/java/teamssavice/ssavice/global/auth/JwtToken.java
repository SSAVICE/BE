package teamssavice.ssavice.global.auth;

import lombok.Builder;

@Builder
public record JwtToken(
        String accessToken,
        Long expiresIn,
        String refreshToken
) {
}
