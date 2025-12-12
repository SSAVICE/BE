package teamssavice.ssavice.global.auth;

import lombok.Builder;

@Builder
public record Token(
        String accessToken,
        Long expiresIn,
        String refreshToken
) {
}
