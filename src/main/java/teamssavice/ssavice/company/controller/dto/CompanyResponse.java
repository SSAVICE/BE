package teamssavice.ssavice.company.controller.dto;

import lombok.Builder;
import teamssavice.ssavice.auth.constants.Role;
import teamssavice.ssavice.company.service.dto.CompanyModel;

public class CompanyResponse {

    @Builder
    public record Login(
            String accessToken,
            Long expiresIn,
            String refreshToken,
            Role role
    ) {
        public static CompanyResponse.Login from(CompanyModel.Login model) {
            return CompanyResponse.Login.builder()
                    .accessToken(model.accessToken())
                    .expiresIn(model.expiresIn())
                    .refreshToken(model.refreshToken())
                    .role(model.role())
                    .build();
        }
    }

    @Builder
    public record Token(
            String accessToken,
            Long expiresIn,
            String refreshToken
    ) {
        public static CompanyResponse.Token from(CompanyModel.Refresh model) {
            return CompanyResponse.Token.builder()
                    .accessToken(model.accessToken())
                    .expiresIn(model.expiresIn())
                    .refreshToken(model.refreshToken())
                    .build();
        }
    }
}
