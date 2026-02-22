package teamssavice.ssavice.company.token;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CompanySignupVerifyTokenService {

    private final CompanySignupVerifyTokenProvider provider;

    public CompanySignupVerifyToken issueToken(Long accountId, String businessNumber, String startDate, String name, String businessName) {
        return provider.createToken(accountId, businessNumber, startDate, name, businessName);
    }

    public void validate(Long accountId, String businessNumber, String startDate, String name, String businessName, String token) {
        Claims claims = provider.parse(token);
        provider.validateToken(claims, accountId, businessNumber, startDate, name, businessName);
    }
}

