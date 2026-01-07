package teamssavice.ssavice.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.auth.Token;
import teamssavice.ssavice.auth.constants.Role;
import teamssavice.ssavice.auth.service.TokenService;
import teamssavice.ssavice.global.constants.ErrorCode;
import teamssavice.ssavice.global.exception.ConflictException;
import teamssavice.ssavice.imageresource.constants.ImageConstants;
import teamssavice.ssavice.imageresource.entity.ImageResource;
import teamssavice.ssavice.imageresource.service.ImageReadService;
import teamssavice.ssavice.s3.S3Service;
import teamssavice.ssavice.s3.event.S3EventDto;
import teamssavice.ssavice.user.entity.Users;
import teamssavice.ssavice.user.service.dto.UserCommand;
import teamssavice.ssavice.user.service.dto.UserModel;

@Service
@RequiredArgsConstructor
public class UserService {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final TokenService tokenService;
    private final UserWriteService userWriteService;
    private final UserReadService userReadService;
    private final ImageReadService imageReadService;
    private final S3Service s3Service;

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

    @Transactional(readOnly = true)
    public UserModel.Info getProfile(Long userId) {
        // 사용자 정보 조회
        Users user = userReadService.findByIdFetchJoinAddressAndImageResource(userId);
        if (user.hasImageResource()) {
            String presignedUrl = s3Service.generateGetPresignedUrl(
                user.getImageResource().getObjectKey());
            return UserModel.Info.from(user, presignedUrl);
        }

        return UserModel.Info.from(user, ImageConstants.DEFAULT_PROFILE_IMAGE_OBJECT_KEY);
    }

    @Transactional
    public UserModel.Modify modifyProfile(Long userId, UserCommand.Modify command) {
        // 사용자 정보 조회
        Users user = userReadService.findById(userId);

        // 이메일이 변경되는 경우만 중복 체크
        if (!user.getEmail().equals(command.email()) && userReadService.existsByEmail(
            command.email())) {
            throw new ConflictException(ErrorCode.USER_EMAIL_ALREADY_EXISTS);
        }

        user.modify(command.name(), command.email(), command.phoneNumber());
        return UserModel.Modify.from(user);
    }

    @Transactional
    public void updateProfileImage(Long userId, String objectKey) {
        Users user = userReadService.findByIdFetchJoinImageResource(userId);
        ImageResource imageResource = imageReadService.findByObjectKey(objectKey);
        if (user.hasImageResource()) {
            applicationEventPublisher.publishEvent(
                S3EventDto.UpdateTag.from(user.getImageResource().getObjectKey(), false));
        }
        user.updateImage(imageResource);
        applicationEventPublisher.publishEvent(S3EventDto.UpdateTag.from(objectKey, true));
    }
}
