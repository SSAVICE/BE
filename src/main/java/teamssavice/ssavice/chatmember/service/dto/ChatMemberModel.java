package teamssavice.ssavice.chatmember.service.dto;

import lombok.Builder;

public class ChatMemberModel {

    @Builder
    public record MemberInfo(
        Long accountId,
        String name,
        String imageUrl
    ) {
        public static MemberInfo of(Long accountId, String name, String imageUrl) {
            return MemberInfo.builder()
                .accountId(accountId)
                .name(name)
                .imageUrl(imageUrl)
                .build();
        }
    }
}
