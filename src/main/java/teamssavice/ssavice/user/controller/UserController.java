package teamssavice.ssavice.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import teamssavice.ssavice.auth.constants.Role;
import teamssavice.ssavice.global.annotation.CurrentId;
import teamssavice.ssavice.global.annotation.RequireRole;
import teamssavice.ssavice.imageresource.ImageRequest;
import teamssavice.ssavice.imageresource.ImageResponse;
import teamssavice.ssavice.imageresource.constants.ImageContentType;
import teamssavice.ssavice.imageresource.constants.ImagePath;
import teamssavice.ssavice.imageresource.service.ImageService;
import teamssavice.ssavice.imageresource.service.dto.ImageModel;
import teamssavice.ssavice.user.controller.dto.UserRequest;
import teamssavice.ssavice.user.controller.dto.UserResponse;
import teamssavice.ssavice.user.service.UserService;
import teamssavice.ssavice.user.service.dto.UserModel;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final ImageService imageService;

    @PostMapping("/login")
    public ResponseEntity<UserResponse.Login> login(
            @RequestBody @Valid UserRequest.Login request
    ) {
        UserModel.Login model = userService.register(request.token());

        return ResponseEntity.ok(UserResponse.Login.from(model));
    }

    @PostMapping("/profile/image")
    @RequireRole(Role.USER)
    public ResponseEntity<ImageResponse.Presigned> createProfilePresignedUrl(
            @CurrentId Long userId,
            @RequestBody @Valid ImageRequest.ContentType request
    ) {
        ImageModel.PutPresigned model = imageService.updateProfileImage(userId, ImagePath.profile, ImageContentType.from(request.contentType()));
        return ResponseEntity.ok(ImageResponse.Presigned.from(model));
    }

    @PostMapping("/profile/image/confirm")
    @RequireRole(Role.USER)
    public ResponseEntity<Void> confirmProfileImageUpload(
            @CurrentId Long userId,
            @RequestBody @Valid ImageRequest.Confirm request
    ) {
        userService.updateProfileImage(userId, request.objectKey());
        return ResponseEntity.ok().build();
    }
}
