package teamssavice.ssavice.auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import teamssavice.ssavice.auth.controller.dto.AuthResponse;
import teamssavice.ssavice.auth.service.TokenService;
import teamssavice.ssavice.auth.service.dto.AuthModel;
import teamssavice.ssavice.global.annotation.CurrentAuth;
import teamssavice.ssavice.global.annotation.CurrentRefreshToken;
import teamssavice.ssavice.global.annotation.PermitAll;
import teamssavice.ssavice.global.dto.Auth;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final TokenService tokenService;

    @PermitAll
    @GetMapping("token/refresh")
    public ResponseEntity<AuthResponse.Refresh> refresh(
        @CurrentRefreshToken String refreshToken
    ) {
        AuthModel.Refresh model = tokenService.refresh(refreshToken);
        return ResponseEntity.ok(AuthResponse.Refresh.from(model));
    }

    @PermitAll
    @GetMapping("/logout")
    public ResponseEntity<Void> logout(
        @CurrentRefreshToken String refreshToken
    ) {
        tokenService.logout(refreshToken);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/id")
    public ResponseEntity<AuthResponse.Me> me(
        @CurrentAuth Auth auth
    ) {
        return ResponseEntity.ok(AuthResponse.Me.from(auth));
    }
}
