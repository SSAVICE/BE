package teamssavice.ssavice.oauth.service.client;

import teamssavice.ssavice.user.constants.Provider;

public interface OAuthClient {
    Provider getProvider();

    OAuthUserInfo getUserInfo(String accessToken);
}
