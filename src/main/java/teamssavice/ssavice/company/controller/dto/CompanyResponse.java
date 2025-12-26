package teamssavice.ssavice.company.controller.dto;

import lombok.Builder;
import teamssavice.ssavice.company.service.dto.CompanyModel;

public class CompanyResponse {

    @Builder
    public record Login(
            String accessToken,
            Long expiresIn,
            String refreshToken,
            Boolean isRegistered
    ) {
        public static CompanyResponse.Login from(CompanyModel.Login model) {
            return Login.builder()
                    .accessToken(model.accessToken())
                    .expiresIn(model.expiresIn())
                    .refreshToken(model.refreshToken())
                    .isRegistered(model.isRegistered())
                    .build();
        }
    }
}
