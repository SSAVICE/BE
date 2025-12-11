package teamssavice.ssavice.user.dto;

import lombok.Builder;

public class UserResponse {

    @Builder
    public record Token(
            String accessToken,
            Long expiresIn,
            String refreshToken
    ) {
    }
}
