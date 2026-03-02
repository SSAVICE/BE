package teamssavice.ssavice.chatmember.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.chatmember.infrastructure.repository.ChatMemberRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMemberReadService {

    private final ChatMemberRepository chatMemberRepository;

    @Transactional(readOnly = true)
    public List<Long> findAllByRoomIdAndIsLeftFalse(String roomId) {
        return chatMemberRepository.findAllByRoomIdAndIsLeftFalse(roomId);
    }
}
