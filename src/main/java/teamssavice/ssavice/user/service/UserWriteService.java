package teamssavice.ssavice.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import teamssavice.ssavice.user.constants.Provider;
import teamssavice.ssavice.user.constants.UserRole;
import teamssavice.ssavice.user.entity.Users;
import teamssavice.ssavice.user.infrastructure.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserWriteService {
    private final UserRepository userRepository;

    public Users save(String email) {
        Users user = Users.builder()
                .userRole(UserRole.USER)
                .provider(Provider.KAKAO)
                .name("홍길동")
                .email(email)
                .phoneNumber("010-1234-5678")
                .imageUrl("url")
                .build();
        return userRepository.save(user);
    }
}
