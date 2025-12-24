package teamssavice.ssavice.company.service.dto;

import lombok.Builder;
import teamssavice.ssavice.auth.Token;

public class CompanyModel {

    @Builder
    public record Login(
            String accessToken,
            Long expiresIn,
            String refreshToken,
            Boolean isRegistered
    ) {
        public static CompanyModel.Login from(Token token, boolean isRegistered) {
            return CompanyModel.Login.builder()
                    .accessToken(token.accessToken())
                    .expiresIn(token.expiresIn())
                    .refreshToken(token.refreshToken())
                    .isRegistered(isRegistered)
                    .build();
        }
    }
}
