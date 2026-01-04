package teamssavice.ssavice.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.auth.Token;
import teamssavice.ssavice.auth.constants.Role;
import teamssavice.ssavice.auth.service.TokenService;
import teamssavice.ssavice.user.entity.Users;
import teamssavice.ssavice.user.service.dto.UserCommand;
import teamssavice.ssavice.user.service.dto.UserModel;

@Service
@RequiredArgsConstructor
public class UserService {

    private final TokenService tokenService;
    private final UserWriteService userWriteService;
    private final UserReadService userReadService;

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
        Users user = userReadService.findByIdFetchJoinAddress(userId);

        return UserModel.Info.from(user);
    }

    @Transactional
    public UserModel.Info modifyProfile(Long userId, UserCommand.Modify command) {
        // 사용자 정보 조회
        Users user = userReadService.findById(userId);

        // 이메일이 변경되는 경우만 중복 체크
        if (!user.getEmail().equals(command.email())) {
            userReadService.checkEmailExists(command.email());
        }

        user.modify(command.name(), command.email(), command.phoneNumber());
        return UserModel.Info.from(user);
    }
}
