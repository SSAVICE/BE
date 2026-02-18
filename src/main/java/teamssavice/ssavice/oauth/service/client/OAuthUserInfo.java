package teamssavice.ssavice.oauth.service.client;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record OAuthUserInfo(
    @NotNull
    String providerId,
    @NotNull
    String email,
    @NotNull
    String name,
    @NotNull
    String phoneNumber
) {
}
