package teamssavice.ssavice.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import teamssavice.ssavice.auth.Token;
import teamssavice.ssavice.auth.constants.Role;
import teamssavice.ssavice.auth.service.TokenService;
import teamssavice.ssavice.imageresource.entity.ImageResource;
import teamssavice.ssavice.imageresource.service.ImageReadService;
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
    private final ImageReadService imageReadService;

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

    public void updateProfileImage(Long userId, String objectKey) {
        Users user = userReadService.findByIdFetchJoinImageResource(userId);
        ImageResource imageResource = imageReadService.findByObjectKey(objectKey);
        if (user.getImageResource() != null) {
            s3Service.updateIsActiveTag(user.getImageResource().getObjectKey(), false);
        }
        userWriteService.updateProfileImage(user, imageResource);
        s3Service.updateIsActiveTag(objectKey, true);
    }
}
