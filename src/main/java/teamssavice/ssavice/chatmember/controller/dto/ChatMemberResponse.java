package teamssavice.ssavice.chatmember.controller.dto;

import lombok.Builder;
import teamssavice.ssavice.chatmember.service.dto.ChatMemberModel;

import java.util.List;

public class ChatMemberResponse {

    @Builder
    public record Members(
        List<MemberInfo> members
    ) {
        public static Members from(List<ChatMemberModel.MemberInfo> models) {
            return Members.builder()
                .members(models.stream().map(MemberInfo::from).toList())
                .build();
        }
    }

    @Builder
    public record MemberInfo(
        Long accountId,
        String name,
        String imageUrl
    ) {
        public static MemberInfo from(ChatMemberModel.MemberInfo model) {
            return MemberInfo.builder()
                .accountId(model.accountId())
                .name(model.name())
                .imageUrl(model.imageUrl())
                .build();
        }
    }
}
