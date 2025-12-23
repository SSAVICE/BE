package teamssavice.ssavice.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import teamssavice.ssavice.auth.controller.dto.AuthRequest;
import teamssavice.ssavice.auth.controller.dto.AuthResponse;
import teamssavice.ssavice.auth.service.TokenService;
import teamssavice.ssavice.auth.service.dto.AuthModel;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final TokenService tokenService;

    @PostMapping("token/refresh")
    public ResponseEntity<AuthResponse.Refresh> refresh(
            @RequestBody @Valid AuthRequest.Refresh request
    ) {
        AuthModel.Refresh model = tokenService.refresh(request.refreshToken());
        return ResponseEntity.ok(AuthResponse.Refresh.from(model));
    }
}
