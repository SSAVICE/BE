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
import teamssavice.ssavice.global.exception.AuthenticationException;
import teamssavice.ssavice.global.property.CompanySignupVerifyTokenProperties;

@Component
@RequiredArgsConstructor
public class CompanySignupVerifyTokenProvider {

    private static final String PURPOSE = "BUSINESS_VERIFY";

    private final CompanySignupVerifyTokenProperties properties;

    public CompanySignupVerifyToken createToken(Long userId, String businessNumber) {
        Date now = new Date();
        long expiresIn = properties.validityInMilliseconds();
        Date exp = new Date(now.getTime() + expiresIn);

        SecretKey key = Keys.hmacShaKeyFor(properties.secretKey().getBytes(StandardCharsets.UTF_8));

        String jwt = Jwts.builder()
            .setSubject(String.valueOf(userId))
            .claim("purpose", PURPOSE)
            .claim("businessNumber", businessNumber)
            .setIssuedAt(now)
            .setExpiration(exp)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();

        return CompanySignupVerifyToken.builder()
            .token(jwt)
            .expiresIn(expiresIn)
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
            throw new AuthenticationException(ErrorCode.INVALID_TOKEN);
        } catch (ExpiredJwtException e) {
            throw new AuthenticationException(ErrorCode.EXPIRED_TOKEN);
        } catch (UnsupportedJwtException e) {
            throw new AuthenticationException(ErrorCode.UNSUPPORTED_TOKEN);
        } catch (IllegalArgumentException e) {
            throw new AuthenticationException(ErrorCode.MISSING_TOKEN);
        } catch (Exception e) {
            throw new AuthenticationException(ErrorCode.UNKNOWN_TOKEN_ERROR);
        }
    }

    public void validatePurpose(Claims claims) {
        String purpose = claims.get("purpose", String.class);
        if (!PURPOSE.equals(purpose)) {
            throw new AuthenticationException(ErrorCode.UNSUPPORTED_TOKEN);
        }
    }
}
