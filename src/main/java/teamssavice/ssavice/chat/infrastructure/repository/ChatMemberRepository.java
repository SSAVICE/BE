package teamssavice.ssavice.chat.infrastructure.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import teamssavice.ssavice.chat.entity.ChatMember;

public interface ChatMemberRepository extends JpaRepository<ChatMember, Long> {

    List<ChatMember> findAllByRoomIdAndIsLeftFalse(String roomId);
}
