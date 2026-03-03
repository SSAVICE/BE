package teamssavice.ssavice.oauth.service;

import org.springframework.stereotype.Service;
import teamssavice.ssavice.global.constants.ErrorCode;
import teamssavice.ssavice.global.exception.AuthenticationException;
import teamssavice.ssavice.oauth.service.client.OAuthClient;
import teamssavice.ssavice.oauth.service.client.OAuthUserInfo;
import teamssavice.ssavice.account.constants.Provider;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OAuthReadService {

    private final Map<Provider, OAuthClient> clients;

    public OAuthReadService(List<OAuthClient> oAuthClients) {
        this.clients = oAuthClients.stream()
            .collect(Collectors.toMap(OAuthClient::getProvider, c -> c));
    }

    public OAuthUserInfo getUserInfo(Provider provider, String accessToken) {
        OAuthClient client = clients.get(provider);
        if (client == null) {
            throw new AuthenticationException(ErrorCode.UNSUPPORTED_PROVIDER);
        }
        return client.getUserInfo(accessToken);
    }
}
