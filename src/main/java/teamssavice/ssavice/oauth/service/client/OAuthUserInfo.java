package teamssavice.ssavice.oauth.service.client;

import lombok.Builder;

@Builder
public record OAuthUserInfo(
    String providerId,
    String email,
    String name,
    String phoneNumber
) {
}
