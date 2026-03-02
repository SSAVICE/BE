package teamssavice.ssavice.chatmember.infrastructure.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChatMemberRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<Long> findAllByRoomIdAndIsLeftFalse(String roomId) {
        return jdbcTemplate.queryForList(
                "SELECT subject FROM chat_member WHERE room_id = ? AND is_left = false",
                Long.class, roomId)
            .stream()
            .toList();
    }
}
