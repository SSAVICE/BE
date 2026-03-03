package teamssavice.ssavice.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.address.AddressCommand;
import teamssavice.ssavice.address.AddressModel;
import teamssavice.ssavice.auth.Token;
import teamssavice.ssavice.auth.constants.Role;
import teamssavice.ssavice.auth.service.TokenService;
import teamssavice.ssavice.global.constants.ErrorCode;
import teamssavice.ssavice.global.exception.ConflictException;
import teamssavice.ssavice.imageresource.entity.ImageResource;
import teamssavice.ssavice.imageresource.service.ImageReadService;
import teamssavice.ssavice.oauth.service.OAuthReadService;
import teamssavice.ssavice.oauth.service.client.OAuthUserInfo;
import teamssavice.ssavice.region.Region;
import teamssavice.ssavice.region.RegionReadService;
import teamssavice.ssavice.s3.S3Service;
import teamssavice.ssavice.s3.event.S3EventDto;
import teamssavice.ssavice.account.constants.Provider;
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
    private final RegionReadService regionReadService;
    private final OAuthReadService oAuthReadService;

    public UserModel.Login register(String oAuthToken, Provider provider) {
        OAuthUserInfo oAuthUserInfo = oAuthReadService.getUserInfo(provider, oAuthToken);

        // user 저장 및 중복 체크
        Users user = userWriteService.findOrCreate(oAuthUserInfo, provider);

        // 토큰 발행
        Token token = tokenService.issueToken(user.getId(), Role.USER);
        return UserModel.Login.from(token);
    }

    @Transactional(readOnly = true)
    public UserModel.Info getProfile(Long userId) {
        // 사용자 정보 조회
        Users user = userReadService.findByIdFetchJoinAddressAndImageResource(userId);
        String presignedUrl = s3Service.generateGetPresignedUrl(user.getObjectKey());
        return UserModel.Info.from(user, presignedUrl);
    }

    @Transactional
    public UserModel.Modify modifyProfile(UserCommand.Modify command) {
        // 사용자 정보 조회
        Users user = userReadService.findById(command.userId());

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
        ImageResource imageResource = imageReadService.findBySourceKey(objectKey);
        imageResource.checkedConfirmed();
        if (user.hasImageResource()) {
            applicationEventPublisher.publishEvent(S3EventDto.Delete.from(user.getImageResource()));
        }
        user.updateImage(imageResource);
        applicationEventPublisher.publishEvent(S3EventDto.Move.from(imageResource));
    }

    @Transactional(readOnly = true)
    public AddressModel.RegionDetail getUserAddress(Long userId) {
        Users user = userReadService.findByIdFetchJoinAddress(userId);
        return AddressModel.RegionDetail.from(user.getAddress());
    }

    @Transactional
    public AddressModel.RegionDetail updateUserAddress(AddressCommand.Update command) {
        Users user = userReadService.findById(command.userId());
        Region region = regionReadService.findByRegionCode(command.regionCode());
        userWriteService.updateAddress(user, region, command);
        return AddressModel.RegionDetail.from(user.getAddress());
    }

}
