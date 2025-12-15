package teamssavice.ssavice.fixture;

import teamssavice.ssavice.auth.Token;

public class TokenFixture {

    public static Token token() {
        return Token.builder()
                .accessToken("accessToken")
                .expiresIn(3600L)
                .refreshToken("refreshToken")
                .build();
    }

    public static Token of(String accessToken, Long expiresIn, String refreshToken) {
        return Token.builder()
                .accessToken(accessToken)
                .expiresIn(expiresIn)
                .refreshToken(refreshToken)
                .build();
    }
}
