package teamssavice.ssavice.user.service;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import teamssavice.ssavice.account.constants.Provider;
import teamssavice.ssavice.account.entity.Account;
import teamssavice.ssavice.account.infrastructure.repository.AccountRepository;
import teamssavice.ssavice.auth.constants.Role;
import teamssavice.ssavice.fixture.ImageResourceFixture;
import teamssavice.ssavice.fixture.UserFixture;
import teamssavice.ssavice.global.config.QueryDSLConfig;
import teamssavice.ssavice.imageresource.entity.ImageResource;
import teamssavice.ssavice.imageresource.infrastructure.repository.ImageResourceRepository;
import teamssavice.ssavice.oauth.service.client.OAuthUserInfo;
import teamssavice.ssavice.user.constants.UserRole;
import teamssavice.ssavice.user.entity.Users;
import teamssavice.ssavice.user.infrastructure.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({QueryDSLConfig.class})
class UserWriteServiceTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ImageResourceRepository imageResourceRepository;
    @Autowired
    EntityManager em;

    private UserWriteService userWriteService;

    @BeforeEach
    void setUp() {
        userWriteService = new UserWriteService(userRepository, accountRepository);
    }

    @Test
    @DisplayName("이미지 active 테스트")
    void updateProfileImageTest() {
        // given
        Account account = accountRepository.save(UserFixture.account());
        Users user = userRepository.save(UserFixture.user(account));
        ImageResource imageResource = imageResourceRepository.save(ImageResourceFixture.imageResource());
        boolean originalActive = imageResource.isActive();

        // when
        user.updateImage(imageResource);
        em.flush();
        em.clear();
        var actual = imageResourceRepository.findById(imageResource.getId()).get();

        // then
        assertThat(originalActive).isFalse();
        assertThat(actual.isActive()).isTrue();
    }

    @Nested
    @DisplayName("findOrCreate 메서드")
    class FindOrCreate {

        @Test
        @DisplayName("성공: Account와 Users가 이미 존재하면 기존 Users를 반환한다")
        void success_whenUserAlreadyExists() {
            // given
            Account account = accountRepository.save(UserFixture.account());
            Users savedUser = userRepository.save(UserFixture.user(account));
            OAuthUserInfo oAuthUserInfo = OAuthUserInfo.builder()
                    .providerId("1234567890")
                    .email("existing@kakao.com")
                    .name("기존유저")
                    .phoneNumber("010-1234-5678")
                    .build();

            // when
            Users result = userWriteService.findOrCreate(oAuthUserInfo, Provider.KAKAO);

            // then
            assertThat(result.getId()).isEqualTo(savedUser.getId());
        }

        @Test
        @DisplayName("성공: Account가 없으면 Account와 Users를 새로 생성한다")
        void success_whenAccountNotExists() {
            // given
            OAuthUserInfo oAuthUserInfo = OAuthUserInfo.builder()
                    .providerId("99999999")
                    .email("new@kakao.com")
                    .name("신규유저")
                    .phoneNumber("010-9999-8888")
                    .build();

            // when
            Users result = userWriteService.findOrCreate(oAuthUserInfo, Provider.KAKAO);

            // then
            assertThat(result.getId()).isNotNull();
            assertThat(result.getName()).isEqualTo("신규유저");
            assertThat(result.getEmail()).isEqualTo("new@kakao.com");
            assertThat(result.getPhoneNumber()).isEqualTo("010-9999-8888");
            assertThat(result.getUserRole()).isEqualTo(UserRole.USER);
            assertThat(result.getAccount().getProviderId()).isEqualTo("99999999");
            assertThat(result.getAccount().getProvider()).isEqualTo(Provider.KAKAO);
            assertThat(result.getAccount().getRole()).isEqualTo(Role.USER);
        }

        @Test
        @DisplayName("성공: Account는 있지만 Users가 없으면 Users만 새로 생성한다")
        void success_whenAccountExistsButUserNot() {
            // given
            Account account = accountRepository.save(Account.builder()
                    .provider(Provider.KAKAO)
                    .providerId("77777777")
                    .role(Role.USER)
                    .build());
            OAuthUserInfo oAuthUserInfo = OAuthUserInfo.builder()
                    .providerId("77777777")
                    .email("mapped@kakao.com")
                    .name("매핑유저")
                    .phoneNumber("010-7777-6666")
                    .build();

            // when
            Users result = userWriteService.findOrCreate(oAuthUserInfo, Provider.KAKAO);

            // then
            assertThat(result.getId()).isEqualTo(account.getId());
            assertThat(result.getName()).isEqualTo("매핑유저");
            assertThat(result.getAccount()).isEqualTo(account);
        }

        @Test
        @DisplayName("성공: 탈퇴된 Account로 재가입 시 Account의 isDeleted가 false로 복구된다")
        void success_whenDeletedAccountRestored() {
            // given
            Account deletedAccount = accountRepository.save(Account.builder()
                    .provider(Provider.KAKAO)
                    .providerId("55555555")
                    .role(Role.USER)
                    .build());
            deletedAccount.deleteAccount();
            accountRepository.save(deletedAccount);

            OAuthUserInfo oAuthUserInfo = OAuthUserInfo.builder()
                    .providerId("55555555")
                    .email("restored@kakao.com")
                    .name("복구유저")
                    .phoneNumber("010-5555-4444")
                    .build();

            // when
            Users result = userWriteService.findOrCreate(oAuthUserInfo, Provider.KAKAO);

            // then
            assertThat(result).isNotNull();
            assertThat(deletedAccount.isDeleted()).isFalse();
        }

        @Test
        @DisplayName("성공: 탈퇴된 Account로 재가입 시 기존 Users를 반환한다")
        void success_returnsExistingUser_whenDeletedAccountRestored() {
            // given
            Account account = accountRepository.save(UserFixture.account());
            Users savedUser = userRepository.save(UserFixture.user(account));
            account.deleteAccount();
            accountRepository.save(account);

            OAuthUserInfo oAuthUserInfo = OAuthUserInfo.builder()
                    .providerId("1234567890") // UserFixture.account()의 providerId
                    .email("restored@kakao.com")
                    .name("복구유저")
                    .phoneNumber("010-1234-5678")
                    .build();

            // when
            Users result = userWriteService.findOrCreate(oAuthUserInfo, Provider.KAKAO);

            // then
            assertThat(result.getId()).isEqualTo(savedUser.getId());
            assertThat(account.isDeleted()).isFalse();
        }
    }
}
