package teamssavice.ssavice.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import teamssavice.ssavice.global.annotation.CurrentId;
import teamssavice.ssavice.image.constants.ImageContentType;
import teamssavice.ssavice.s3.PresignedModel;
import teamssavice.ssavice.s3.PresignedRequest;
import teamssavice.ssavice.s3.PresignedResponse;
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
    public ResponseEntity<UserResponse.Login> login(
            @RequestBody @Valid UserRequest.Login request
    ) {
        UserModel.Login model = userService.register(request.token());

        return ResponseEntity.ok(UserResponse.Login.from(model));
    }

    @PostMapping("/profile/image")
    public ResponseEntity<PresignedResponse> createPresignedUrl(
            @CurrentId Long userId,
            @RequestBody @Valid PresignedRequest request
    ) {
        PresignedModel model = userService.uploadProfileImage(userId, ImageContentType.from(request.contentType()));
        return ResponseEntity.ok(PresignedResponse.from(model));
    }
}
