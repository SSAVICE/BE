package teamssavice.ssavice.auth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import teamssavice.ssavice.auth.constants.Role;
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
            throw new AuthenticationException("유효하지 않은 토큰입니다.");
        } catch (ExpiredJwtException e) {
            throw new AuthenticationException("만료된 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            throw new AuthenticationException("지원되지 않는 토큰입니다.");
        } catch (IllegalArgumentException e) {
            throw new AuthenticationException("토큰이 존재하지 않습니다.");
        } catch (Exception e) {
            throw new AuthenticationException("알 수 없는 토큰 에러");
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
