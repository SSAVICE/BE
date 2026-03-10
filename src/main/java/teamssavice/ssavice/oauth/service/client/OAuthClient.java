package teamssavice.ssavice.oauth.service.client;

import teamssavice.ssavice.account.constants.Provider;

public interface OAuthClient {
    Provider getProvider();

    OAuthUserInfo getUserInfo(String accessToken);

    void unlink(String providerId);
}
