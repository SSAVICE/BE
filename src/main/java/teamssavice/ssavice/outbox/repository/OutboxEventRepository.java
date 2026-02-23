package teamssavice.ssavice.outbox.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import teamssavice.ssavice.outbox.entity.OutboxEvent;

import java.util.List;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {
    List<OutboxEvent> findByPublishedFalseOrderByCreatedAtAsc();
}