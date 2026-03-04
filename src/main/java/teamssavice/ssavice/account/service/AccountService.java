package teamssavice.ssavice.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import teamssavice.ssavice.account.entity.Account;
import teamssavice.ssavice.oauth.service.OAuthWriteService;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountReadService accountReadService;
    private final AccountWriteService accountWriteService;
    private final OAuthWriteService oAuthWriteService;

    public void delete(Long accountId, String oAuthAccessToken) {
        Account account = accountReadService.findById(accountId);
        oAuthWriteService.unlink(account.getProvider(), oAuthAccessToken);
        accountWriteService.delete(accountId);
    }
}
