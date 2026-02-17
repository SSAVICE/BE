package teamssavice.ssavice.company.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import teamssavice.ssavice.global.constants.ErrorCode;
import teamssavice.ssavice.global.exception.BusinessAuthenticationException;
import teamssavice.ssavice.global.property.CompanySignupVerifyTokenProperties;

@Component
@RequiredArgsConstructor
public class CompanySignupVerifyTokenProvider {

    private static final String PURPOSE = "BUSINESS_VERIFY";

    private final CompanySignupVerifyTokenProperties properties;

    public CompanySignupVerifyToken createToken(Long userId, String businessNumber, String startDate, String name, String businessName) {
        Date now = new Date();

        SecretKey key = Keys.hmacShaKeyFor(properties.secretKey().getBytes(StandardCharsets.UTF_8));

        String jwt = Jwts.builder()
            .setSubject(String.valueOf(userId))
            .claim("purpose", PURPOSE)
            .claim("businessNumber", businessNumber)
            .claim("startDate", startDate)
            .claim("name", name)
            .claim("businessName", businessName)
            .setIssuedAt(now)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();

        return CompanySignupVerifyToken.builder()
            .token(jwt)
            .build();
    }

    public Claims parse(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(
                properties.secretKey().getBytes(StandardCharsets.UTF_8));
            return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        } catch (SecurityException | MalformedJwtException e) {
            throw new BusinessAuthenticationException(ErrorCode.INVALID_TOKEN);
        } catch (UnsupportedJwtException e) {
            throw new BusinessAuthenticationException(ErrorCode.UNSUPPORTED_TOKEN);
        } catch (IllegalArgumentException e) {
            throw new BusinessAuthenticationException(ErrorCode.MISSING_TOKEN);
        } catch (Exception e) {
            throw new BusinessAuthenticationException(ErrorCode.UNKNOWN_TOKEN_ERROR);
        }
    }

    public void validateToken(Claims claims, Long userId, String businessNumber, String startDate, String name, String businessName) {

        String purpose = claims.get("purpose", String.class);
        if (!PURPOSE.equals(purpose)) {
            throw new BusinessAuthenticationException(ErrorCode.UNSUPPORTED_TOKEN);
        }
        if (!claims.getSubject().equals(String.valueOf(userId))) {
            throw new BusinessAuthenticationException(ErrorCode.INVALID_TOKEN);
        }
        if (!claims.get("businessNumber", String.class).equals(businessNumber)) {
            throw new BusinessAuthenticationException(ErrorCode.INVALID_TOKEN);
        }
        if (!claims.get("startDate", String.class).equals(startDate)) {
            throw new BusinessAuthenticationException(ErrorCode.INVALID_TOKEN);
        }
        if (!claims.get("name", String.class).equals(name)) {
            throw new BusinessAuthenticationException(ErrorCode.INVALID_TOKEN);
        }
        if (!claims.get("businessName", String.class).equals(businessName)) {
            throw new BusinessAuthenticationException(ErrorCode.INVALID_TOKEN);
        }
    }
}
