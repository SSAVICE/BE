package teamssavice.ssavice.auth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import teamssavice.ssavice.auth.constants.Role;
import teamssavice.ssavice.global.constants.ErrorCode;
import teamssavice.ssavice.global.exception.AuthenticationException;
import teamssavice.ssavice.global.property.TokenProperties;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class TokenProvider {

    private final TokenProperties tokenProperties;

    public Token createToken(Long id, Role role) {
        Date now = new Date();
        Date accessValidity = new Date(now.getTime() + tokenProperties.accessValidityInMilliseconds());
        SecretKey key = Keys.hmacShaKeyFor(tokenProperties.secretKey().getBytes(StandardCharsets.UTF_8));
        String refresh = generateRefreshToken();
        String access = Jwts.builder()
                .setSubject(String.valueOf(id))
                .claim("role", role.name())
                .setIssuedAt(now)
                .setExpiration(accessValidity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return Token.builder()
                .accessToken(access)
                .expiresIn(tokenProperties.accessValidityInMilliseconds())
                .refreshToken(refresh)
                .build();
    }

    public Claims getClaim(String accessToken) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(tokenProperties.secretKey().getBytes(StandardCharsets.UTF_8));
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
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

    public String generateRefreshToken() {
        byte[] randomBytes = new byte[32];
        SecureRandom sr = new SecureRandom();
        sr.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    public String hashRefreshToken(String refreshToken) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(tokenProperties.hashSecretKey().getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(keySpec);
            byte[] macBytes = mac.doFinal(refreshToken.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(macBytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
