package teamssavice.ssavice.user.service;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import teamssavice.ssavice.fixture.ImageResourceFixture;
import teamssavice.ssavice.fixture.UserFixture;
import teamssavice.ssavice.global.config.QueryDSLConfig;
import teamssavice.ssavice.imageresource.entity.ImageResource;
import teamssavice.ssavice.imageresource.infrastructure.repository.ImageResourceRepository;
import teamssavice.ssavice.oauth.service.client.OAuthUserInfo;
import teamssavice.ssavice.user.constants.Provider;
import teamssavice.ssavice.user.constants.UserRole;
import teamssavice.ssavice.user.entity.Users;
import teamssavice.ssavice.user.infrastructure.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@DataJpaTest
@Import({QueryDSLConfig.class, UserWriteService.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserWriteServiceTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ImageResourceRepository imageResourceRepository;
    @Autowired
    EntityManager em;

    private Users user;
    private ImageResource imageResource;

    @BeforeEach
    void setUp() {
        user = UserFixture.user();
        imageResource = ImageResourceFixture.imageResource();
    }

    @Test
    @DisplayName("이미지 active 테스트")
    void updateProfileImageTest() {
        // given
        Users user = userRepository.save(this.user);
        ImageResource imageResource = imageResourceRepository.save(this.imageResource);
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

        private UserWriteService userWriteServiceUnit;

        @Mock
        private UserRepository userRepositoryMock;

        private AutoCloseable mocks;

        @BeforeEach
        void setUp() {
            mocks = MockitoAnnotations.openMocks(this);
            userWriteServiceUnit = new UserWriteService(userRepositoryMock);
        }

        @Test
        @DisplayName("성공: 이미 존재하는 사용자면 저장하지 않고 기존 사용자를 반환한다")
        void success_whenUserAlreadyExists() {
            // given
            OAuthUserInfo oAuthUserInfo = OAuthUserInfo.builder()
                    .providerId("12345678")
                    .email("existing@kakao.com")
                    .name("기존유저")
                    .phoneNumber("010-1234-5678")
                    .build();
            Provider provider = Provider.KAKAO;

            Users existingUser = Users.builder()
                    .userRole(UserRole.USER)
                    .provider(provider)
                    .name("기존유저")
                    .email("existing@kakao.com")
                    .phoneNumber("010-1234-5678")
                    .providerId("12345678")
                    .build();

            given(userRepositoryMock.findByProviderIdAndProvider("12345678", provider))
                    .willReturn(Optional.of(existingUser));

            // when
            Users result = userWriteServiceUnit.findOrCreate(oAuthUserInfo, provider);

            // then
            assertThat(result).isEqualTo(existingUser);
            verify(userRepositoryMock, never()).save(any());
        }

        @Test
        @DisplayName("성공: 존재하지 않는 사용자면 새로 저장하여 반환한다")
        void success_whenUserNotExists() {
            // given
            OAuthUserInfo oAuthUserInfo = OAuthUserInfo.builder()
                    .providerId("99999999")
                    .email("new@kakao.com")
                    .name("신규유저")
                    .phoneNumber("010-9999-8888")
                    .build();
            Provider provider = Provider.KAKAO;

            Users newUser = Users.builder()
                    .userRole(UserRole.USER)
                    .provider(provider)
                    .name("신규유저")
                    .email("new@kakao.com")
                    .phoneNumber("010-9999-8888")
                    .providerId("99999999")
                    .build();

            given(userRepositoryMock.findByProviderIdAndProvider("99999999", provider))
                    .willReturn(Optional.empty());
            given(userRepositoryMock.save(any(Users.class))).willReturn(newUser);

            // when
            Users result = userWriteServiceUnit.findOrCreate(oAuthUserInfo, provider);

            // then
            assertThat(result).isEqualTo(newUser);
            verify(userRepositoryMock).save(any(Users.class));
        }

        @Test
        @DisplayName("성공: 새 사용자 저장 시 OAuthUserInfo의 모든 필드가 올바르게 매핑된다")
        void success_newUserHasCorrectFields() {
            // given
            OAuthUserInfo oAuthUserInfo = OAuthUserInfo.builder()
                    .providerId("77777777")
                    .email("mapped@kakao.com")
                    .name("매핑유저")
                    .phoneNumber("010-7777-6666")
                    .build();
            Provider provider = Provider.KAKAO;

            ArgumentCaptor<Users> userCaptor = ArgumentCaptor.forClass(Users.class);

            given(userRepositoryMock.findByProviderIdAndProvider("77777777", provider))
                    .willReturn(Optional.empty());
            given(userRepositoryMock.save(userCaptor.capture())).willAnswer(inv -> inv.getArgument(0));

            // when
            userWriteServiceUnit.findOrCreate(oAuthUserInfo, provider);

            // then
            Users savedUser = userCaptor.getValue();
            assertThat(savedUser.getProviderId()).isEqualTo("77777777");
            assertThat(savedUser.getEmail()).isEqualTo("mapped@kakao.com");
            assertThat(savedUser.getName()).isEqualTo("매핑유저");
            assertThat(savedUser.getPhoneNumber()).isEqualTo("010-7777-6666");
            assertThat(savedUser.getProvider()).isEqualTo(Provider.KAKAO);
            assertThat(savedUser.getUserRole()).isEqualTo(UserRole.USER);
        }

        @Test
        @DisplayName("성공: providerId와 provider 조합으로 사용자를 조회한다")
        void success_queriesByProviderIdAndProvider() {
            // given
            OAuthUserInfo oAuthUserInfo = OAuthUserInfo.builder()
                    .providerId("55555555")
                    .email("query@kakao.com")
                    .name("쿼리유저")
                    .phoneNumber("010-5555-4444")
                    .build();
            Provider provider = Provider.KAKAO;

            Users existingUser = Users.builder()
                    .userRole(UserRole.USER)
                    .provider(provider)
                    .name("쿼리유저")
                    .email("query@kakao.com")
                    .phoneNumber("010-5555-4444")
                    .providerId("55555555")
                    .build();

            given(userRepositoryMock.findByProviderIdAndProvider("55555555", Provider.KAKAO))
                    .willReturn(Optional.of(existingUser));

            // when
            userWriteServiceUnit.findOrCreate(oAuthUserInfo, provider);

            // then
            verify(userRepositoryMock).findByProviderIdAndProvider("55555555", Provider.KAKAO);
        }
    }

    @Nested
    @DisplayName("findOrCreate를 통한 save 검증")
    class SaveViaFindOrCreate {

        private UserWriteService userWriteServiceUnit;

        @Mock
        private UserRepository userRepositoryMock;

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);
            userWriteServiceUnit = new UserWriteService(userRepositoryMock);
        }

        @Test
        @DisplayName("성공: 유저가 없으면 OAuthUserInfo로 새 Users 엔티티를 생성하고 저장한다")
        void success() {
            // given
            OAuthUserInfo oAuthUserInfo = OAuthUserInfo.builder()
                    .providerId("11111111")
                    .email("save@kakao.com")
                    .name("저장유저")
                    .phoneNumber("010-1111-2222")
                    .build();
            Provider provider = Provider.KAKAO;

            given(userRepositoryMock.findByProviderIdAndProvider("11111111", Provider.KAKAO))
                .willReturn(Optional.empty());
            ArgumentCaptor<Users> userCaptor = ArgumentCaptor.forClass(Users.class);
            given(userRepositoryMock.save(userCaptor.capture())).willAnswer(inv -> inv.getArgument(0));

            // when
            Users result = userWriteServiceUnit.findOrCreate(oAuthUserInfo, provider);

            // then
            Users saved = userCaptor.getValue();
            assertThat(result).isNotNull();
            assertThat(saved.getEmail()).isEqualTo("save@kakao.com");
            assertThat(saved.getName()).isEqualTo("저장유저");
            assertThat(saved.getPhoneNumber()).isEqualTo("010-1111-2222");
            assertThat(saved.getProviderId()).isEqualTo("11111111");
            assertThat(saved.getProvider()).isEqualTo(Provider.KAKAO);
            assertThat(saved.getUserRole()).isEqualTo(UserRole.USER);
        }
    }
}
