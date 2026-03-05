package teamssavice.ssavice.oauth.service;

import org.springframework.stereotype.Service;
import teamssavice.ssavice.account.constants.Provider;
import teamssavice.ssavice.global.constants.ErrorCode;
import teamssavice.ssavice.global.exception.AuthenticationException;
import teamssavice.ssavice.oauth.service.client.OAuthClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OAuthWriteService {

    private final Map<Provider, OAuthClient> clients;

    public OAuthWriteService(List<OAuthClient> oAuthClients) {
        this.clients = oAuthClients.stream()
            .collect(Collectors.toMap(OAuthClient::getProvider, c -> c));
    }

    public void unlink(Provider provider, String providerId) {
        OAuthClient client = clients.get(provider);
        if (client == null) {
            throw new AuthenticationException(ErrorCode.UNSUPPORTED_PROVIDER);
        }
        client.unlink(providerId);
    }
}
