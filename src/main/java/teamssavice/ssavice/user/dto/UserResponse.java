package teamssavice.ssavice.user.dto;

import lombok.Builder;
import teamssavice.ssavice.user.service.dto.UserModel;

public class UserResponse {

    @Builder
    public record Token(
            String accessToken,
            Long expiresIn,
            String refreshToken
    ) {
        public static UserResponse.Token from(UserModel.Login model) {
            return UserResponse.Token.builder()
                    .accessToken(model.accessToken())
                    .expiresIn(model.expiresIn())
                    .refreshToken(model.refreshToken())
                    .build();
        }
    }
}
