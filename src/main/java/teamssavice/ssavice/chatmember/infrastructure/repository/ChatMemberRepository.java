package teamssavice.ssavice.chatmember.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import teamssavice.ssavice.chatmember.entity.ChatMember;

import java.util.List;

public interface ChatMemberRepository extends JpaRepository<ChatMember, Long> {

    List<ChatMember> findAllByRoomIdAndIsLeftFalse(String roomId);
}
