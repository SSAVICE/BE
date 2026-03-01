package teamssavice.ssavice.chat.service.dto;

import lombok.Builder;

public class ChatModel {

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
