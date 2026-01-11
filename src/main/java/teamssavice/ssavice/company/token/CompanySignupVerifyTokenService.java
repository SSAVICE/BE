package teamssavice.ssavice.company.token;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import teamssavice.ssavice.global.constants.ErrorCode;
import teamssavice.ssavice.global.exception.AuthenticationException;

@Component
@RequiredArgsConstructor
public class CompanySignupVerifyTokenService {

    private final CompanySignupVerifyTokenProvider provider;

    public CompanySignupVerifyToken issueToken(Long userId, String businessNumber) {
        return provider.createToken(userId, businessNumber);
    }

    public void validate(Long userId, String businessNumber, String token) {
        Claims claims = provider.parse(token);
        provider.validatePurpose(claims);

        if (!claims.getSubject().equals(String.valueOf(userId))) {
            throw new AuthenticationException(ErrorCode.INVALID_TOKEN);
        }
        if (!claims.get("businessNumber", String.class).equals(businessNumber)) {
            throw new AuthenticationException(ErrorCode.INVALID_TOKEN);
        }
    }
}

