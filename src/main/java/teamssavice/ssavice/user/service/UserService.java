package teamssavice.ssavice.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import teamssavice.ssavice.auth.Token;
import teamssavice.ssavice.auth.constants.Role;
import teamssavice.ssavice.auth.service.TokenService;
import teamssavice.ssavice.image.constants.ImageContentType;
import teamssavice.ssavice.image.constants.ImagePath;
import teamssavice.ssavice.image.service.ImageService;
import teamssavice.ssavice.s3.PresignedModel;
import teamssavice.ssavice.s3.S3ObjectKeyGenerator;
import teamssavice.ssavice.s3.S3Service;
import teamssavice.ssavice.user.entity.Users;
import teamssavice.ssavice.user.service.dto.UserModel;

@Service
@RequiredArgsConstructor
public class UserService {
    private final TokenService tokenService;
    private final UserWriteService userWriteService;
    private final UserReadService userReadService;
    private final S3Service s3Service;
    private final ImageService imageService;
    private final S3ObjectKeyGenerator s3ObjectKeyGenerator;

    public UserModel.Login register(String kakaoToken) {
        // 토큰 검증
        String email = "test@test.com";

        // user 저장 및 중복 체크
        Users user = userReadService.findByEmail(email)
                .orElseGet(() -> userWriteService.save(email));

        // 토큰 발행
        Token token = tokenService.issueToken(user.getId(), Role.USER);
        return UserModel.Login.from(token);
    }

    public PresignedModel uploadProfileImage(Long userId, ImageContentType contentType) {
        String objectKey = s3ObjectKeyGenerator.generator(ImagePath.profile, userId, contentType);
        imageService.save(objectKey, ImagePath.profile, contentType);
        return s3Service.createPresignedUrl(objectKey, contentType);
    }
}
