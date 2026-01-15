package teamssavice.ssavice.company.token;

import lombok.Builder;

@Builder
public record CompanySignupVerifyToken(
    String token,
    Long expiresIn
) {

}
