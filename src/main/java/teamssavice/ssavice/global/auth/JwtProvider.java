package teamssavice.ssavice.global.auth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import teamssavice.ssavice.global.exception.AuthenticationException;
import teamssavice.ssavice.global.property.JwtProperties;
import teamssavice.ssavice.user.constants.Role;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtProperties jwtProperties;

    public JwtToken createToken(Long id, Role role) {
        Date now = new Date();
        Date accessValidity = new Date(now.getTime() + jwtProperties.accessValidityInMilliseconds());
        SecretKey key = Keys.hmacShaKeyFor(jwtProperties.secretKey().getBytes(StandardCharsets.UTF_8));
        String refresh = UUID.randomUUID().toString();
        String access = Jwts.builder()
                .setSubject(String.valueOf(id))
                .claim("role", role.name())
                .claim("token_use", "access")
                .setIssuedAt(now)
                .setExpiration(accessValidity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return JwtToken.builder()
                .accessToken(access)
                .expiresIn(jwtProperties.accessValidityInMilliseconds())
                .refreshToken(refresh)
                .build();
    }

    public Claims getClaim(String accessToken) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtProperties.secretKey().getBytes(StandardCharsets.UTF_8));
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (SecurityException | MalformedJwtException e) {
            throw new JwtException("유효하지 않은 토큰입니다.");
        } catch (ExpiredJwtException e) {
            throw new JwtException("만료된 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            throw new JwtException("지원되지 않는 토큰입니다.");
        } catch (IllegalArgumentException e) {
            throw new JwtException("토큰이 존재하지 않습니다.");
        } catch (Exception e) {
            throw new AuthenticationException("알 수 없는 토큰 에러");
        }
    }
}
