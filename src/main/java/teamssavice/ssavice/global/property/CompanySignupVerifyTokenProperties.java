package teamssavice.ssavice.global.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "company.signup.verify-token")
public record CompanySignupVerifyTokenProperties(
    String secretKey,
    long validityInMilliseconds
) {

}
