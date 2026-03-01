package teamssavice.ssavice.fixture;

import teamssavice.ssavice.chatmember.entity.ChatMember;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ChatMemberFixture {

    public static ChatMember chatMember(Long subject) {
        ChatMember member = mock(ChatMember.class);
        when(member.getSubject()).thenReturn(subject);
        return member;
    }
}
