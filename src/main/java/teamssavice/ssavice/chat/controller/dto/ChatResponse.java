package teamssavice.ssavice.chat.controller.dto;

import java.util.List;
import lombok.Builder;
import teamssavice.ssavice.chat.service.dto.ChatModel;

public class ChatResponse {

    @Builder
    public record Members(
        List<MemberInfo> members
    ) {
        public static Members from(List<ChatModel.MemberInfo> models) {
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
        public static MemberInfo from(ChatModel.MemberInfo model) {
            return MemberInfo.builder()
                .accountId(model.accountId())
                .name(model.name())
                .imageUrl(model.imageUrl())
                .build();
        }
    }
}
