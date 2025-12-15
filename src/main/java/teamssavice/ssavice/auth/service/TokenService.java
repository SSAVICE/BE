package teamssavice.ssavice.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import teamssavice.ssavice.auth.Token;
import teamssavice.ssavice.auth.TokenProvider;
import teamssavice.ssavice.auth.constants.Role;
import teamssavice.ssavice.global.constants.ErrorCode;
import teamssavice.ssavice.global.exception.AuthenticationException;
import teamssavice.ssavice.user.entity.RefreshToken;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenProvider tokenProvider;

    private final Map<String, RefreshToken> refreshTokenMap = new ConcurrentHashMap<>();

    public Token createToken(Long userId, Role role) {
        return tokenProvider.createToken(userId, role);
    }

    public void saveRefreshToken(Long userId, Token token, Role role) {
        String hashed = tokenProvider.hashRefreshToken(token.refreshToken());
        RefreshToken refreshToken = RefreshToken.builder()
                .userId(userId)
                .issuedAt(new Date())
                .expiresIn(token.expiresIn())
                .role(role)
                .build();
        refreshTokenMap.put(hashed, refreshToken);
    }

    public RefreshToken getRefreshToken(String refreshToken) {
        String hashed = tokenProvider.hashRefreshToken(refreshToken);
        if(!refreshTokenMap.containsKey(hashed)) throw new AuthenticationException(ErrorCode.INVALID_TOKEN);
        RefreshToken token = refreshTokenMap.get(hashed);
        if(token.isRevoked()) throw new AuthenticationException(ErrorCode.INVALID_TOKEN);
        if(token.isExpired()) throw new AuthenticationException(ErrorCode.EXPIRED_TOKEN);

        return token;
    }
}
