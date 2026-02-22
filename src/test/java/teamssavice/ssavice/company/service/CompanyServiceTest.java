package teamssavice.ssavice.company.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import teamssavice.ssavice.account.constants.Provider;
import teamssavice.ssavice.account.entity.Account;
import teamssavice.ssavice.account.service.AccountReadService;
import teamssavice.ssavice.address.AddressCommand;
import teamssavice.ssavice.auth.Token;
import teamssavice.ssavice.auth.constants.Role;
import teamssavice.ssavice.auth.service.TokenService;
import teamssavice.ssavice.company.entity.Company;
import teamssavice.ssavice.company.service.dto.CompanyCommand;
import teamssavice.ssavice.company.service.dto.CompanyModel;
import teamssavice.ssavice.company.token.CompanySignupVerifyTokenService;
import teamssavice.ssavice.fixture.AddressFixture;
import teamssavice.ssavice.fixture.CompanyFixture;
import teamssavice.ssavice.global.constants.ErrorCode;
import teamssavice.ssavice.global.exception.ConflictException;
import teamssavice.ssavice.oauth.service.OAuthReadService;
import teamssavice.ssavice.oauth.service.client.OAuthUserInfo;
import teamssavice.ssavice.region.Region;
import teamssavice.ssavice.region.RegionReadService;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @InjectMocks
    private CompanyService companyService;

    @Mock
    private TokenService tokenService;
    @Mock
    private AccountReadService accountReadService;
    @Mock
    private CompanyReadService companyReadService;
    @Mock
    private CompanyWriteService companyWriteService;
    @Mock
    private OAuthReadService oAuthReadService;
    @Mock
    private CompanySignupVerifyTokenService companySignupVerifyTokenService;
    @Mock
    private RegionReadService regionReadService;

    private Account companyAccount;
    private OAuthUserInfo oAuthUserInfo;
    private Token token;

    @BeforeEach
    void setUp() {
        companyAccount = CompanyFixture.account();
        ReflectionTestUtils.setField(companyAccount, "id", 1L);

        oAuthUserInfo = OAuthUserInfo.builder()
                .providerId("1234567890")
                .email("company@kakao.com")
                .name("사장님")
                .phoneNumber("010-1234-5678")
                .build();

        token = new Token("access-token", 3600L, "refresh-token");
    }

    @Nested
    @DisplayName("login 메서드")
    class Login {

        @Test
        @DisplayName("성공: Company가 등록되지 않은 경우 isRegistered=false를 반환한다")
        void success_notRegistered() {
            // given
            given(oAuthReadService.getUserInfo(Provider.KAKAO, "oauth-token"))
                    .willReturn(oAuthUserInfo);
            given(companyWriteService.findOrCreateAccount(oAuthUserInfo, Provider.KAKAO))
                    .willReturn(companyAccount);
            given(companyReadService.findOptionalById(companyAccount.getId()))
                    .willReturn(Optional.empty());
            given(tokenService.issueToken(companyAccount.getId(), Role.COMPANY))
                    .willReturn(token);

            // when
            CompanyModel.Login result = companyService.login("oauth-token", Provider.KAKAO);

            // then
            assertThat(result.isRegistered()).isFalse();
            assertThat(result.accessToken()).isEqualTo("access-token");
        }

        @Test
        @DisplayName("성공: Company가 등록된 경우 isRegistered=true를 반환한다")
        void success_registered() {
            // given
            Company company = CompanyFixture.company(companyAccount, AddressFixture.address());
            ReflectionTestUtils.setField(company, "id", 1L);

            given(oAuthReadService.getUserInfo(Provider.KAKAO, "oauth-token"))
                    .willReturn(oAuthUserInfo);
            given(companyWriteService.findOrCreateAccount(oAuthUserInfo, Provider.KAKAO))
                    .willReturn(companyAccount);
            given(companyReadService.findOptionalById(companyAccount.getId()))
                    .willReturn(Optional.of(company));
            given(tokenService.issueToken(company.getId(), Role.COMPANY))
                    .willReturn(token);

            // when
            CompanyModel.Login result = companyService.login("oauth-token", Provider.KAKAO);

            // then
            assertThat(result.isRegistered()).isTrue();
            assertThat(result.accessToken()).isEqualTo("access-token");
        }
    }

    @Nested
    @DisplayName("register 메서드")
    class Register {

        @Test
        @DisplayName("성공: Company 등록 후 isRegistered=true와 토큰을 반환한다")
        void success() {
            // given
            CompanyCommand.Create command = CompanyCommand.Create.builder()
                    .accountId(1L)
                    .verifyToken("verify-token")
                    .companyName("테스트업체")
                    .businessName("(주)테스트")
                    .startDate("2025-01-01")
                    .ownerName("사장님")
                    .phoneNumber("010-1234-5678")
                    .businessNumber("0123456789")
                    .depositor("사장님")
                    .accountNumber("1234567890")
                    .regionCode("2723011100")
                    .postCode("41566")
                    .address("대구광역시 북구")
                    .detailAddress("상세주소")
                    .longitude(BigDecimal.valueOf(128.611010))
                    .latitude(BigDecimal.valueOf(35.891926))
                    .build();

            Company company = CompanyFixture.company(companyAccount, AddressFixture.address());
            ReflectionTestUtils.setField(company, "id", 1L);

            Region region = Region.builder()
                    .gugun("북구")
                    .region("산격동")
                    .regionCode("2723011100")
                    .build();

            given(accountReadService.findById(1L)).willReturn(companyAccount);
            given(regionReadService.findByRegionCode("2723011100")).willReturn(region);
            given(companyWriteService.save(eq(command), eq(companyAccount), any(AddressCommand.RegionInfo.class)))
                    .willReturn(company);
            given(tokenService.issueToken(company.getId(), Role.COMPANY)).willReturn(token);

            // when
            CompanyModel.Login result = companyService.register(command);

            // then
            assertThat(result.isRegistered()).isTrue();
            assertThat(result.accessToken()).isEqualTo("access-token");
            verify(companySignupVerifyTokenService).validate(
                    1L, "0123456789", "2025-01-01", "사장님", "(주)테스트", "verify-token");
            verify(companyReadService).checkAccountExists(companyAccount.getId());
        }

        @Test
        @DisplayName("실패: 이미 등록된 Account로 register 시 예외 발생")
        void fail_alreadyExists() {
            // given
            CompanyCommand.Create command = CompanyCommand.Create.builder()
                    .accountId(1L)
                    .verifyToken("verify-token")
                    .companyName("테스트업체")
                    .businessName("(주)테스트")
                    .startDate("2025-01-01")
                    .ownerName("사장님")
                    .phoneNumber("010-1234-5678")
                    .businessNumber("0123456789")
                    .depositor("사장님")
                    .accountNumber("1234567890")
                    .regionCode("2723011100")
                    .postCode("41566")
                    .address("대구광역시 북구")
                    .detailAddress("상세주소")
                    .longitude(BigDecimal.valueOf(128.611010))
                    .latitude(BigDecimal.valueOf(35.891926))
                    .build();

            given(accountReadService.findById(1L)).willReturn(companyAccount);
            org.mockito.BDDMockito.willThrow(new ConflictException(ErrorCode.COMPANY_ALREADY_EXISTS))
                    .given(companyReadService).checkAccountExists(companyAccount.getId());

            // when & then
            assertThatThrownBy(() -> companyService.register(command))
                    .isInstanceOf(ConflictException.class);
        }
    }
}
