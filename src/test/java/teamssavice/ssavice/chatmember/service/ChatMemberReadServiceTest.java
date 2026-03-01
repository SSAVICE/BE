package teamssavice.ssavice.chatmember.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import teamssavice.ssavice.chatmember.entity.ChatMember;
import teamssavice.ssavice.chatmember.infrastructure.repository.ChatMemberRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ChatMemberReadServiceTest {

    @InjectMocks
    private ChatMemberReadService chatMemberReadService;

    @Mock
    private ChatMemberRepository chatMemberRepository;

    /**
     * ChatMember는 @NoArgsConstructor(access = AccessLevel.PROTECTED)이므로
     * new ChatMember()를 직접 호출할 수 없다.
     * Mockito.mock()을 사용하여 인스턴스를 생성한다.
     * ChatReadService는 repository 반환값을 그대로 전달하므로 내부 상태를 stubbing할 필요가 없다.
     */
    private ChatMember createChatMember() {
        return mock(ChatMember.class);
    }

    @Nested
    @DisplayName("findAllByRoomIdAndIsLeftFalse 메서드")
    class FindAllByRoomIdAndIsLeftFalse {

        @Test
        @DisplayName("성공: 활성 멤버 목록을 반환한다")
        void success() {
            // given
            String roomId = "room-123";
            ChatMember member1 = createChatMember();
            ChatMember member2 = createChatMember();

            given(chatMemberRepository.findAllByRoomIdAndIsLeftFalse(roomId))
                .willReturn(List.of(member1, member2));

            // when
            List<ChatMember> result = chatMemberReadService.findAllByRoomIdAndIsLeftFalse(roomId);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(member1, member2);
            then(chatMemberRepository).should().findAllByRoomIdAndIsLeftFalse(roomId);
        }

        @Test
        @DisplayName("성공: 채팅방에 활성 멤버가 없으면 빈 목록을 반환한다")
        void success_emptyList() {
            // given
            String roomId = "room-empty";

            given(chatMemberRepository.findAllByRoomIdAndIsLeftFalse(roomId))
                .willReturn(List.of());

            // when
            List<ChatMember> result = chatMemberReadService.findAllByRoomIdAndIsLeftFalse(roomId);

            // then
            assertThat(result).isEmpty();
            then(chatMemberRepository).should().findAllByRoomIdAndIsLeftFalse(roomId);
        }

        @Test
        @DisplayName("성공: 단일 활성 멤버가 있는 경우 해당 멤버를 반환한다")
        void success_singleMember() {
            // given
            String roomId = "room-single";
            ChatMember member = createChatMember();

            given(chatMemberRepository.findAllByRoomIdAndIsLeftFalse(roomId))
                .willReturn(List.of(member));

            // when
            List<ChatMember> result = chatMemberReadService.findAllByRoomIdAndIsLeftFalse(roomId);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0)).isEqualTo(member);
        }
    }
}
