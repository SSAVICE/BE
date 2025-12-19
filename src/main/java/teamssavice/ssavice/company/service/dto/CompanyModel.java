package teamssavice.ssavice.company.service.dto;

import lombok.Builder;
import teamssavice.ssavice.auth.Token;
import teamssavice.ssavice.auth.constants.Role;

public class CompanyModel {

    @Builder
    public record Login(
            String accessToken,
            Long expiresIn,
            String refreshToken,
            Role role
    ) {
        public static CompanyModel.Login from(Token token, Role role) {
            return CompanyModel.Login.builder()
                    .accessToken(token.accessToken())
                    .expiresIn(token.expiresIn())
                    .refreshToken(token.refreshToken())
                    .role(role)
                    .build();
        }
    }

    @Builder
    public record Refresh(
            String accessToken,
            Long expiresIn,
            String refreshToken
    ) {
        public static CompanyModel.Refresh from(Token token) {
            return CompanyModel.Refresh.builder()
                    .accessToken(token.accessToken())
                    .expiresIn(token.expiresIn())
                    .refreshToken(token.refreshToken())
                    .build();
        }
    }
}
