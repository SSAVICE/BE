package teamssavice.ssavice.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import teamssavice.ssavice.address.AddressModel;
import teamssavice.ssavice.address.AddressRequest;
import teamssavice.ssavice.address.AddressResponse;
import teamssavice.ssavice.auth.constants.Role;
import teamssavice.ssavice.global.annotation.CurrentId;
import teamssavice.ssavice.global.annotation.RequireRole;
import teamssavice.ssavice.imageresource.ImageRequest;
import teamssavice.ssavice.imageresource.ImageResponse;
import teamssavice.ssavice.imageresource.constants.ImageContentType;
import teamssavice.ssavice.imageresource.constants.ImagePath;
import teamssavice.ssavice.imageresource.service.ImageService;
import teamssavice.ssavice.imageresource.service.dto.ImageModel;
import teamssavice.ssavice.s3.S3Service;
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
    private final S3Service s3Service;

    @PostMapping("/login")
    public ResponseEntity<UserResponse.Login> login(
        @RequestBody @Valid UserRequest.Login request
    ) {
        UserModel.Login model = userService.register(request.token());

        return ResponseEntity.ok(UserResponse.Login.from(model));
    }

    @GetMapping("/profile")
    @RequireRole(Role.USER)
    public ResponseEntity<UserResponse.Info> profile(
        @CurrentId Long userId
    ) {
        UserModel.Info model = userService.getProfile(userId);

        return ResponseEntity.ok(UserResponse.Info.from(model));
    }

    @PostMapping("/profile")
    @RequireRole(Role.USER)
    public ResponseEntity<UserResponse.Summary> Modify(
        @CurrentId Long userId,
        @RequestBody @Valid UserRequest.Modify request
    ) {
        UserModel.Modify model = userService.modifyProfile(request.toCommand(userId));

        return ResponseEntity.ok(UserResponse.Summary.from(model));
    }

    @PostMapping("/profile/image")
    @RequireRole(Role.USER)
    public ResponseEntity<ImageResponse.PresignedUrl> createProfilePresignedUrl(
        @CurrentId Long userId,
        @RequestBody @Valid ImageRequest.ContentType request
    ) {
        ImageModel.PutPresignedUrl model = imageService.updateImage(userId, ImagePath.profile,
            ImageContentType.from(request.contentType()));
        return ResponseEntity.ok(ImageResponse.PresignedUrl.from(model));
    }

    @PostMapping("/profile/image/confirm")
    @RequireRole(Role.USER)
    public ResponseEntity<Void> confirmProfileImageUpload(
        @CurrentId Long userId,
        @RequestBody @Valid ImageRequest.Confirm request
    ) {
        s3Service.validateTempImageOrDelete(request.objectKey());
        userService.updateProfileImage(userId, request.objectKey());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/address")
    @RequireRole(Role.USER)
    public ResponseEntity<AddressResponse.RegionDetail> getAddress(
        @CurrentId Long userId
    ) {
        AddressModel.RegionDetail model = userService.getUserAddress(userId);
        return ResponseEntity.ok(AddressResponse.RegionDetail.from(model));
    }

    @PatchMapping("/address")
    @RequireRole(Role.USER)
    public ResponseEntity<AddressResponse.RegionDetail> patchAddress(
        @CurrentId Long userId,
        @RequestBody @Valid AddressRequest.Region request
    ) {
        AddressModel.RegionDetail model = userService.updateUserAddress(request.toCommand(userId));
        return ResponseEntity.ok(AddressResponse.RegionDetail.from(model));
    }
}
