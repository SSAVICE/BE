package teamssavice.ssavice.user.service.dto;

import lombok.Builder;
import teamssavice.ssavice.global.auth.JwtToken;

public class UserModel {

    @Builder
    public record Login(
            String accessToken,
            Long expiresIn,
            String refreshToken
    ) {
        public static Login from(JwtToken token) {
            return UserModel.Login.builder()
                    .accessToken(token.accessToken())
                    .expiresIn(token.expiresIn())
                    .refreshToken(token.refreshToken())
                    .build();
        }
    }
}
