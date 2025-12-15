package teamssavice.ssavice.auth;

import lombok.Builder;

@Builder
public record Token(
        String accessToken,
        Long expiresIn,
        String refreshToken
) {
}
