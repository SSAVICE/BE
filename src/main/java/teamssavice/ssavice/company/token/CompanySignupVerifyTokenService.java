package teamssavice.ssavice.company.token;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CompanySignupVerifyTokenService {

    private final CompanySignupVerifyTokenProvider provider;

    public CompanySignupVerifyToken issueToken(Long userId, String businessNumber, String startDate, String name, String businessName) {
        return provider.createToken(userId, businessNumber, startDate, name, businessName);
    }

    public void validate(Long userId, String businessNumber, String startDate, String name, String businessName, String token) {
        Claims claims = provider.parse(token);
        provider.validateToken(claims, userId, businessNumber, startDate, name, businessName);
    }
}

