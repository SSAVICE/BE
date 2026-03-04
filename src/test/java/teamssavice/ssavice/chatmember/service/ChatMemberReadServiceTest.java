package teamssavice.ssavice.chatmember.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import teamssavice.ssavice.chatmember.infrastructure.repository.ChatMemberRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ChatMemberReadServiceTest {

    @InjectMocks
    private ChatMemberReadService chatMemberReadService;

    @Mock
    private ChatMemberRepository chatMemberRepository;

    @Nested
    @DisplayName("findAllByRoomIdAndIsLeftFalse 메서드")
    class FindAllByRoomIdAndIsLeftFalse {

        @Test
        @DisplayName("성공: 활성 멤버 목록을 Long ID 리스트로 반환한다")
        void success() {
            // given
            String roomId = "room-123";

            given(chatMemberRepository.findAllByRoomIdAndIsLeftFalse(roomId))
                .willReturn(List.of(1L, 2L));

            // when
            List<Long> result = chatMemberReadService.findAllByRoomIdAndIsLeftFalse(roomId);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(1L, 2L);
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
            List<Long> result = chatMemberReadService.findAllByRoomIdAndIsLeftFalse(roomId);

            // then
            assertThat(result).isEmpty();
            then(chatMemberRepository).should().findAllByRoomIdAndIsLeftFalse(roomId);
        }

        @Test
        @DisplayName("성공: 단일 활성 멤버가 있는 경우 해당 멤버 ID를 반환한다")
        void success_singleMember() {
            // given
            String roomId = "room-single";

            given(chatMemberRepository.findAllByRoomIdAndIsLeftFalse(roomId))
                .willReturn(List.of(1L));

            // when
            List<Long> result = chatMemberReadService.findAllByRoomIdAndIsLeftFalse(roomId);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0)).isEqualTo(1L);
        }
    }
}
