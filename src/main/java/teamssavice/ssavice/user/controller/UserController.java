package teamssavice.ssavice.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import teamssavice.ssavice.user.dto.UserRequest;
import teamssavice.ssavice.user.dto.UserResponse;
import teamssavice.ssavice.user.service.UserService;
import teamssavice.ssavice.user.service.dto.UserModel;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<UserResponse.Token> login(
            @RequestBody @Valid UserRequest.Login request
    ) {
        UserModel.Login model = userService.register(request.token());

        return ResponseEntity.ok(UserResponse.Token.builder()
                .accessToken(model.accessToken())
                .expiresIn(model.expiresIn())
                .refreshToken(model.refreshToken())
                .build());
    }
}
