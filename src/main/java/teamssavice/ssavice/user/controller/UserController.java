package teamssavice.ssavice.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import teamssavice.ssavice.user.controller.dto.UserRequest;
import teamssavice.ssavice.user.controller.dto.UserResponse;
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

        return ResponseEntity.ok(UserResponse.Token.from(model));
    }

    @PostMapping("/refresh")
    public ResponseEntity<UserResponse.Token> refresh(
        @RequestBody @Valid UserRequest.Refresh request
    ) {
        UserModel.Login model = userService.refresh(request.refreshToken());
        return ResponseEntity.ok(UserResponse.Token.from(model));
    }
}
