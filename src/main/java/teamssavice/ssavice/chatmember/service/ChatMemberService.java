package teamssavice.ssavice.chatmember.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.account.entity.Account;
import teamssavice.ssavice.account.service.AccountReadService;
import teamssavice.ssavice.auth.constants.Role;
import teamssavice.ssavice.chatmember.service.dto.ChatMemberModel;
import teamssavice.ssavice.company.service.CompanyReadService;
import teamssavice.ssavice.global.constants.ErrorCode;
import teamssavice.ssavice.global.exception.ForbiddenException;
import teamssavice.ssavice.s3.S3Service;
import teamssavice.ssavice.user.service.UserReadService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatMemberService {

    private final ChatMemberReadService chatMemberReadService;
    private final AccountReadService accountReadService;
    private final UserReadService userReadService;
    private final CompanyReadService companyReadService;
    private final S3Service s3Service;

    @Transactional(readOnly = true)
    public List<ChatMemberModel.MemberInfo> getRoomMemberInfos(String roomId, Long authId) {
        List<Long> memberIds = chatMemberReadService.findAllByRoomIdAndIsLeftFalse(roomId);

        boolean isMember = memberIds.stream()
            .anyMatch(memberId -> memberId.equals(authId));
        if (!isMember) {
            throw new ForbiddenException(ErrorCode.CHAT_ROOM_ACCESS_DENIED);
        }

        Map<Role, List<Long>> idsByRole = groupIdsByRole(memberIds);

        List<ChatMemberModel.MemberInfo> result = new ArrayList<>();
        result.addAll(toUserMemberInfos(idsByRole.getOrDefault(Role.USER, List.of())));
        result.addAll(toCompanyMemberInfos(idsByRole.getOrDefault(Role.COMPANY, List.of())));
        return result;
    }

    private Map<Role, List<Long>> groupIdsByRole(List<Long> memberIds) {
        Map<Role, List<Long>> idsByRole = new HashMap<>();

        List<Account> accounts = accountReadService.findAllByIdIn(memberIds);
        for (Account account : accounts) {
            idsByRole.computeIfAbsent(account.getRole(), k -> new ArrayList<>())
                .add(account.getId());
        }

        return idsByRole;
    }

    private List<ChatMemberModel.MemberInfo> toUserMemberInfos(List<Long> userIds) {
        if (userIds.isEmpty()) return List.of();

        return userReadService.findAllByIdInFetchJoinImageResource(userIds).stream()
            .map(user -> ChatMemberModel.MemberInfo.of(
                user.getId(), user.getName(), s3Service.generateGetPresignedUrl(user.getObjectKey())))
            .toList();
    }

    private List<ChatMemberModel.MemberInfo> toCompanyMemberInfos(List<Long> companyIds) {
        if (companyIds.isEmpty()) return List.of();

        return companyReadService.findAllByIdInFetchJoinImageResource(companyIds).stream()
            .map(company -> ChatMemberModel.MemberInfo.of(
                company.getId(), company.getCompanyName(), s3Service.generateGetPresignedUrl(company.getObjectKey())))
            .toList();
    }
}
