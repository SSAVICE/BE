package teamssavice.ssavice.company.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import teamssavice.ssavice.account.constants.Provider;
import teamssavice.ssavice.account.entity.Account;
import teamssavice.ssavice.account.infrastructure.repository.AccountRepository;
import teamssavice.ssavice.address.AddressCommand;
import teamssavice.ssavice.auth.constants.Role;
import teamssavice.ssavice.company.entity.Company;
import teamssavice.ssavice.company.infrastructure.repository.CompanyRepository;
import teamssavice.ssavice.company.service.dto.CompanyCommand;
import teamssavice.ssavice.global.constants.ErrorCode;
import teamssavice.ssavice.global.exception.EntityNotFoundException;
import teamssavice.ssavice.oauth.service.client.OAuthUserInfo;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class CompanyWriteServiceTest {

    @InjectMocks
    private CompanyWriteService companyWriteService;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private AccountRepository accountRepository;

    private OAuthUserInfo oAuthUserInfo;
    private Provider provider;

    @BeforeEach
    void setUp() {
        oAuthUserInfo = OAuthUserInfo.builder()
                .providerId("company-provider-123")
                .email("company@kakao.com")
                .name("업체대표자")
                .phoneNumber("010-1234-5678")
                .build();
        provider = Provider.KAKAO;
    }

    @Nested
    @DisplayName("findOrCreateAccount 메서드")
    class FindOrCreateAccount {

        @Test
        @DisplayName("성공: 동일한 providerId, provider, COMPANY 역할의 Account가 이미 존재하면 기존 Account를 반환한다")
        void success_whenAccountAlreadyExists() {
            // given
            Account existingAccount = Account.builder()
                    .provider(provider)
                    .providerId("company-provider-123")
                    .role(Role.COMPANY)
                    .build();

            given(accountRepository.findByProviderIdAndProviderAndRole("company-provider-123", provider, Role.COMPANY))
                    .willReturn(Optional.of(existingAccount));

            // when
            Account result = companyWriteService.findOrCreateAccount(oAuthUserInfo, provider);

            // then
            assertThat(result).isEqualTo(existingAccount);
            assertThat(result.getRole()).isEqualTo(Role.COMPANY);
            then(accountRepository).should(never()).save(any());
        }

        @Test
        @DisplayName("성공: Account가 존재하지 않으면 COMPANY 역할의 새 Account를 생성하여 반환한다")
        void success_whenAccountNotExists() {
            // given
            Account newAccount = Account.builder()
                    .provider(provider)
                    .providerId("company-provider-123")
                    .role(Role.COMPANY)
                    .build();

            given(accountRepository.findByProviderIdAndProviderAndRole("company-provider-123", provider, Role.COMPANY))
                    .willReturn(Optional.empty());
            given(accountRepository.save(any(Account.class))).willReturn(newAccount);

            // when
            Account result = companyWriteService.findOrCreateAccount(oAuthUserInfo, provider);

            // then
            assertThat(result).isEqualTo(newAccount);
            assertThat(result.getRole()).isEqualTo(Role.COMPANY);
            then(accountRepository).should().save(any(Account.class));
        }

        @Test
        @DisplayName("성공: 새 Account 생성 시 COMPANY 역할, providerId, provider가 올바르게 설정된다")
        void success_verifyAccountFieldsWhenCreated() {
            // given
            given(accountRepository.findByProviderIdAndProviderAndRole("company-provider-123", provider, Role.COMPANY))
                    .willReturn(Optional.empty());
            given(accountRepository.save(any(Account.class))).willAnswer(invocation -> invocation.getArgument(0));

            // when
            Account result = companyWriteService.findOrCreateAccount(oAuthUserInfo, provider);

            // then
            assertThat(result.getRole()).isEqualTo(Role.COMPANY);
            assertThat(result.getProviderId()).isEqualTo("company-provider-123");
            assertThat(result.getProvider()).isEqualTo(Provider.KAKAO);
        }

        @Test
        @DisplayName("성공: USER 역할의 Account가 있어도 COMPANY 역할이 없으면 새 Account를 생성한다")
        void success_whenOnlyUserRoleAccountExists() {
            // given
            // USER 역할 Account는 있지만 COMPANY 역할 Account는 없는 상황
            given(accountRepository.findByProviderIdAndProviderAndRole("company-provider-123", provider, Role.COMPANY))
                    .willReturn(Optional.empty());
            Account newCompanyAccount = Account.builder()
                    .provider(provider)
                    .providerId("company-provider-123")
                    .role(Role.COMPANY)
                    .build();
            given(accountRepository.save(any(Account.class))).willReturn(newCompanyAccount);

            // when
            Account result = companyWriteService.findOrCreateAccount(oAuthUserInfo, provider);

            // then
            assertThat(result.getRole()).isEqualTo(Role.COMPANY);
            then(accountRepository).should().save(any(Account.class));
        }

        @Test
        @DisplayName("성공: 탈퇴된 Account로 재가입 시 Account의 isDeleted가 false로 복구된다")
        void success_whenDeletedAccountRestored() {
            // given
            Account deletedAccount = Account.builder()
                    .provider(provider)
                    .providerId("company-provider-123")
                    .role(Role.COMPANY)
                    .build();
            deletedAccount.deleteAccount();
            assertThat(deletedAccount.isDeleted()).isTrue();

            given(accountRepository.findByProviderIdAndProviderAndRole("company-provider-123", provider, Role.COMPANY))
                    .willReturn(Optional.of(deletedAccount));

            // when
            Account result = companyWriteService.findOrCreateAccount(oAuthUserInfo, provider);

            // then
            assertThat(result.isDeleted()).isFalse();
            then(accountRepository).should(never()).save(any());
        }

        @Test
        @DisplayName("성공: 탈퇴되지 않은 기존 Account는 restore를 호출해도 isDeleted가 false를 유지한다")
        void success_existingActiveAccountNotAffected() {
            // given
            Account existingAccount = Account.builder()
                    .provider(provider)
                    .providerId("company-provider-123")
                    .role(Role.COMPANY)
                    .build();
            assertThat(existingAccount.isDeleted()).isFalse();

            given(accountRepository.findByProviderIdAndProviderAndRole("company-provider-123", provider, Role.COMPANY))
                    .willReturn(Optional.of(existingAccount));

            // when
            Account result = companyWriteService.findOrCreateAccount(oAuthUserInfo, provider);

            // then
            assertThat(result.isDeleted()).isFalse();
        }
    }

    @Nested
    @DisplayName("save 메서드")
    class Save {

        private CompanyCommand.Create command;
        private Account account;
        private AddressCommand.RegionInfo addressCommand;

        @BeforeEach
        void setUp() {
            account = Account.builder()
                    .provider(Provider.KAKAO)
                    .providerId("company-provider-123")
                    .role(Role.COMPANY)
                    .build();

            command = CompanyCommand.Create.builder()
                    .accountId(1L)
                    .companyName("테스트 업체")
                    .businessName("테스트 상호명")
                    .startDate("2020-01-01")
                    .ownerName("홍길동")
                    .phoneNumber("010-1234-5678")
                    .businessNumber("123-45-67890")
                    .description("업체 설명")
                    .depositor("홍길동")
                    .accountNumber("123-456-789")
                    .detail("상세 설명")
                    .latitude(new BigDecimal("37.5665"))
                    .longitude(new BigDecimal("126.9780"))
                    .postCode("04524")
                    .address("서울시 중구 세종대로 110")
                    .detailAddress("1층")
                    .build();

            addressCommand = AddressCommand.RegionInfo.builder()
                    .regionCode("1100000000")
                    .gugunCode("1101000000")
                    .region("서울특별시")
                    .gugun("종로구")
                    .latitude(new BigDecimal("37.5665"))
                    .longitude(new BigDecimal("126.9780"))
                    .postCode("04524")
                    .address("서울시 중구 세종대로 110")
                    .detailAddress("1층")
                    .build();
        }

        @Test
        @DisplayName("성공: command와 account로 Company를 생성하여 저장하고 반환한다")
        void success() {
            // given
            Company savedCompany = Company.builder()
                    .account(account)
                    .companyName("테스트 업체")
                    .businessName("테스트 상호명")
                    .startDate("2020-01-01")
                    .ownerName("홍길동")
                    .phoneNumber("010-1234-5678")
                    .businessNumber("123-45-67890")
                    .description("업체 설명")
                    .depositor("홍길동")
                    .accountNumber("123-456-789")
                    .detail("상세 설명")
                    .build();

            given(companyRepository.save(any(Company.class))).willReturn(savedCompany);

            // when
            Company result = companyWriteService.save(command, account, addressCommand);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getCompanyName()).isEqualTo("테스트 업체");
            assertThat(result.getOwnerName()).isEqualTo("홍길동");
            assertThat(result.getAccount()).isEqualTo(account);
            then(companyRepository).should().save(any(Company.class));
        }

        @Test
        @DisplayName("성공: save() 호출 시 전달된 Account가 Company에 올바르게 연결된다")
        void success_accountMappedToCompany() {
            // given
            given(companyRepository.save(any(Company.class))).willAnswer(invocation -> invocation.getArgument(0));

            // when
            Company result = companyWriteService.save(command, account, addressCommand);

            // then
            assertThat(result.getAccount()).isEqualTo(account);
            assertThat(result.getAccount().getRole()).isEqualTo(Role.COMPANY);
        }

        @Test
        @DisplayName("성공: Address 정보가 command와 addressCommand로부터 올바르게 구성된다")
        void success_addressBuiltCorrectly() {
            // given
            given(companyRepository.save(any(Company.class))).willAnswer(invocation -> invocation.getArgument(0));

            // when
            Company result = companyWriteService.save(command, account, addressCommand);

            // then
            assertThat(result.getAddress()).isNotNull();
            assertThat(result.getAddress().getAddress()).isEqualTo("서울시 중구 세종대로 110");
            assertThat(result.getAddress().getPostCode()).isEqualTo("04524");
            assertThat(result.getAddress().getRegion()).isEqualTo("서울특별시");
            assertThat(result.getAddress().getGugun()).isEqualTo("종로구");
            assertThat(result.getAddress().getLatitude()).isEqualByComparingTo(new BigDecimal("37.5665"));
            assertThat(result.getAddress().getLongitude()).isEqualByComparingTo(new BigDecimal("126.9780"));
        }

        @Test
        @DisplayName("성공: save() 호출 시 GeoHash가 Address에 설정된다")
        void success_geoHashIsSetOnAddress() {
            // given
            given(companyRepository.save(any(Company.class))).willAnswer(invocation -> invocation.getArgument(0));

            // when
            Company result = companyWriteService.save(command, account, addressCommand);

            // then
            assertThat(result.getAddress().getGeoHash()).isNotNull();
            assertThat(result.getAddress().getGeoHash()).isNotBlank();
        }
    }

    @Nested
    @DisplayName("addRating 메서드")
    class AddRating {

        @Test
        @DisplayName("성공: companyId와 score로 평점을 추가하면 updateCount가 1 이상이다")
        void success() {
            // given
            Long companyId = 1L;
            Integer score = 5;
            given(companyRepository.addRating(companyId, score)).willReturn(1);

            // when & then (예외가 발생하지 않아야 함)
            companyWriteService.addRating(companyId, score);

            then(companyRepository).should().addRating(companyId, score);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 companyId로 평점 추가 시 COMPANY_NOT_FOUND 예외를 던진다")
        void fail_whenCompanyNotFound() {
            // given
            Long nonExistentCompanyId = 9999L;
            Integer score = 4;
            given(companyRepository.addRating(nonExistentCompanyId, score)).willReturn(0);

            // when & then
            assertThatThrownBy(() -> companyWriteService.addRating(nonExistentCompanyId, score))
                    .isInstanceOf(EntityNotFoundException.class)
                    .satisfies(ex -> assertThat(((EntityNotFoundException) ex).getErrorCode())
                            .isEqualTo(ErrorCode.COMPANY_NOT_FOUND));
        }

        @Test
        @DisplayName("실패: updateCount가 0이면 COMPANY_NOT_FOUND 예외가 발생한다")
        void fail_whenUpdateCountIsZero() {
            // given
            Long companyId = 1L;
            Integer score = 3;
            given(companyRepository.addRating(companyId, score)).willReturn(0);

            // when & then
            assertThatThrownBy(() -> companyWriteService.addRating(companyId, score))
                    .isInstanceOf(EntityNotFoundException.class)
                    .satisfies(ex -> assertThat(((EntityNotFoundException) ex).getErrorCode())
                            .isEqualTo(ErrorCode.COMPANY_NOT_FOUND));
        }
    }
}
