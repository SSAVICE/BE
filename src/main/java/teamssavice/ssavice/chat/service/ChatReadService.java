package teamssavice.ssavice.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.chat.entity.ChatMember;
import teamssavice.ssavice.chat.infrastructure.repository.ChatMemberRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatReadService {

    private final ChatMemberRepository chatMemberRepository;

    @Transactional(readOnly = true)
    public List<ChatMember> findAllByRoomIdAndIsLeftFalse(String roomId) {
        List<ChatMember> members = chatMemberRepository.findAllByRoomIdAndIsLeftFalse(roomId);
        return members;
    }
}
