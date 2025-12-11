package teamssavice.ssavice.global.auth;

public record JwtToken(
        String accessToken,
        Long expiresIn,
        String refreshToken
) {

}
