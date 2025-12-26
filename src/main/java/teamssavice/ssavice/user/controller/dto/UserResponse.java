package teamssavice.ssavice.user.controller.dto;

import lombok.Builder;
import teamssavice.ssavice.user.service.dto.UserModel;

public class UserResponse {

    @Builder
    public record Login(
            String accessToken,
            Long expiresIn,
            String refreshToken
    ) {
        public static Login from(UserModel.Login model) {
            return Login.builder()
                    .accessToken(model.accessToken())
                    .expiresIn(model.expiresIn())
                    .refreshToken(model.refreshToken())
                    .build();
        }
    }
}
