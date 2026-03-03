package teamssavice.ssavice.chatmember.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import teamssavice.ssavice.account.entity.Account;
import teamssavice.ssavice.account.service.AccountReadService;
import teamssavice.ssavice.chatmember.service.dto.ChatMemberModel;
import teamssavice.ssavice.company.entity.Company;
import teamssavice.ssavice.company.service.CompanyReadService;
import teamssavice.ssavice.fixture.AccountFixture;
import teamssavice.ssavice.fixture.AddressFixture;
import teamssavice.ssavice.fixture.CompanyFixture;
import teamssavice.ssavice.fixture.UserFixture;
import teamssavice.ssavice.s3.S3Service;
import teamssavice.ssavice.user.entity.Users;
import teamssavice.ssavice.user.service.UserReadService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class ChatMemberServiceTest {

    @InjectMocks
    private ChatMemberService chatMemberService;

    @Mock
    private AccountReadService accountReadService;

    @Mock
    private UserReadService userReadService;

    @Mock
    private CompanyReadService companyReadService;

    @Mock
    private S3Service s3Service;

    // ---- 헬퍼 메서드 ----

    private Users createUser(Long id, String name) {
        Account account = AccountFixture.userAccount(id);
        Users user = UserFixture.user(account);
        ReflectionTestUtils.setField(user, "id", id);
        ReflectionTestUtils.setField(user, "name", name);
        return user;
    }

    private Company createCompany(Long id, String companyName) {
        Account account = AccountFixture.companyAccount(id);
        Company company = CompanyFixture.company(account, AddressFixture.address());
        ReflectionTestUtils.setField(company, "id", id);
        ReflectionTestUtils.setField(company, "companyName", companyName);
        return company;
    }

    // ---- 테스트 ----

    @Nested
    @DisplayName("getMemberInfos 메서드")
    class GetMemberInfos {

        @Test
        @DisplayName("성공: User와 Company가 혼합된 멤버 조회 시 각 타입 정보를 올바르게 반환한다")
        void success_mixedUserAndCompany() {
            // given
            Long userId = 1L;
            Long companyId = 2L;
            List<Long> memberIds = List.of(userId, companyId);

            Account userAccount = AccountFixture.userAccount(userId);
            Account companyAccount = AccountFixture.companyAccount(companyId);

            Users user = createUser(userId, "홍길동");
            Company company = createCompany(companyId, "테스트업체");

            String userPresignedUrl = "https://s3.example.com/user1.png";
            String companyPresignedUrl = "https://s3.example.com/company2.png";

            given(accountReadService.findAllByIdIn(memberIds))
                .willReturn(List.of(userAccount, companyAccount));
            given(userReadService.findAllByIdInFetchJoinImageResource(List.of(userId)))
                .willReturn(List.of(user));
            given(companyReadService.findAllByIdInFetchJoinImageResource(List.of(companyId)))
                .willReturn(List.of(company));
            given(s3Service.generateGetPresignedUrl(user.getObjectKey()))
                .willReturn(userPresignedUrl);
            given(s3Service.generateGetPresignedUrl(company.getObjectKey()))
                .willReturn(companyPresignedUrl);

            // when
            List<ChatMemberModel.MemberInfo> result = chatMemberService.getMemberInfos(memberIds);

            // then
            assertThat(result).hasSize(2);

            ChatMemberModel.MemberInfo userInfo = result.stream()
                .filter(info -> info.accountId().equals(userId))
                .findFirst()
                .orElseThrow();
            assertThat(userInfo.name()).isEqualTo("홍길동");
            assertThat(userInfo.imageUrl()).isEqualTo(userPresignedUrl);

            ChatMemberModel.MemberInfo companyInfo = result.stream()
                .filter(info -> info.accountId().equals(companyId))
                .findFirst()
                .orElseThrow();
            assertThat(companyInfo.name()).isEqualTo("테스트업체");
            assertThat(companyInfo.imageUrl()).isEqualTo(companyPresignedUrl);
        }

        @Test
        @DisplayName("성공: User 멤버만 있는 경우 User 정보만 반환하고 CompanyReadService는 호출하지 않는다")
        void success_onlyUsers() {
            // given
            Long userId1 = 1L;
            Long userId2 = 2L;
            List<Long> memberIds = List.of(userId1, userId2);

            Account account1 = AccountFixture.userAccount(userId1);
            Account account2 = AccountFixture.userAccount(userId2);

            Users user1 = createUser(userId1, "사용자A");
            Users user2 = createUser(userId2, "사용자B");

            String presignedUrl1 = "https://s3.example.com/user1.png";
            String presignedUrl2 = "https://s3.example.com/user2.png";

            given(accountReadService.findAllByIdIn(memberIds))
                .willReturn(List.of(account1, account2));
            given(userReadService.findAllByIdInFetchJoinImageResource(List.of(userId1, userId2)))
                .willReturn(List.of(user1, user2));
            given(s3Service.generateGetPresignedUrl(user1.getObjectKey()))
                .willReturn(presignedUrl1);
            given(s3Service.generateGetPresignedUrl(user2.getObjectKey()))
                .willReturn(presignedUrl2);

            // when
            List<ChatMemberModel.MemberInfo> result = chatMemberService.getMemberInfos(memberIds);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).extracting(ChatMemberModel.MemberInfo::name)
                .containsExactlyInAnyOrder("사용자A", "사용자B");

            verifyNoInteractions(companyReadService);
        }

        @Test
        @DisplayName("성공: Company 멤버만 있는 경우 Company 정보만 반환하고 UserReadService는 호출하지 않는다")
        void success_onlyCompanies() {
            // given
            Long companyId1 = 10L;
            Long companyId2 = 20L;
            List<Long> memberIds = List.of(companyId1, companyId2);

            Account account1 = AccountFixture.companyAccount(companyId1);
            Account account2 = AccountFixture.companyAccount(companyId2);

            Company company1 = createCompany(companyId1, "업체A");
            Company company2 = createCompany(companyId2, "업체B");

            String presignedUrl1 = "https://s3.example.com/company1.png";
            String presignedUrl2 = "https://s3.example.com/company2.png";

            given(accountReadService.findAllByIdIn(memberIds))
                .willReturn(List.of(account1, account2));
            given(companyReadService.findAllByIdInFetchJoinImageResource(List.of(companyId1, companyId2)))
                .willReturn(List.of(company1, company2));
            given(s3Service.generateGetPresignedUrl(company1.getObjectKey()))
                .willReturn(presignedUrl1);
            given(s3Service.generateGetPresignedUrl(company2.getObjectKey()))
                .willReturn(presignedUrl2);

            // when
            List<ChatMemberModel.MemberInfo> result = chatMemberService.getMemberInfos(memberIds);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).extracting(ChatMemberModel.MemberInfo::name)
                .containsExactlyInAnyOrder("업체A", "업체B");

            verifyNoInteractions(userReadService);
        }

        @Test
        @DisplayName("성공: 단일 User 멤버만 있는 경우도 정상 조회된다")
        void success_singleMember() {
            // given
            Long userId = 5L;
            List<Long> memberIds = List.of(userId);

            Account account = AccountFixture.userAccount(userId);
            Users user = createUser(userId, "혼자인사용자");

            String presignedUrl = "https://s3.example.com/solo.png";

            given(accountReadService.findAllByIdIn(memberIds))
                .willReturn(List.of(account));
            given(userReadService.findAllByIdInFetchJoinImageResource(List.of(userId)))
                .willReturn(List.of(user));
            given(s3Service.generateGetPresignedUrl(user.getObjectKey()))
                .willReturn(presignedUrl);

            // when
            List<ChatMemberModel.MemberInfo> result = chatMemberService.getMemberInfos(memberIds);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).accountId()).isEqualTo(userId);
            assertThat(result.get(0).name()).isEqualTo("혼자인사용자");
            assertThat(result.get(0).imageUrl()).isEqualTo(presignedUrl);
        }

        @Test
        @DisplayName("성공: 빈 memberIds 리스트를 전달하면 빈 결과를 반환하고 하위 서비스는 호출하지 않는다")
        void success_emptyMemberIds() {
            // given
            List<Long> memberIds = List.of();

            given(accountReadService.findAllByIdIn(memberIds))
                .willReturn(List.of());

            // when
            List<ChatMemberModel.MemberInfo> result = chatMemberService.getMemberInfos(memberIds);

            // then
            assertThat(result).isEmpty();

            verifyNoInteractions(userReadService);
            verifyNoInteractions(companyReadService);
        }
    }
}