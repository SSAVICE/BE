package teamssavice.ssavice.outbox.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import teamssavice.ssavice.outbox.entity.OutboxEvent;
import teamssavice.ssavice.outbox.infrastructure.repository.OutboxEventRepository;
import teamssavice.ssavice.sqs.infrastructure.OutboxEventPublisher;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxScheduler {

    private final OutboxEventRepository outboxEventRepository;
    private final OutboxEventPublisher outboxEventPublisher;

    @Scheduled(fixedDelay = 30000)
    @SchedulerLock(
            name = "outbox_poll_lock",
            lockAtMostFor = "PT1M",
            lockAtLeastFor = "PT30S"
    )
    public void poll() {
        List<OutboxEvent> events = outboxEventRepository.findTop100ByPublishedFalseOrderByCreatedAtAsc();
        log.info("[Outbox] unpublished events: {}", events.size());

        for (OutboxEvent event : events) {
            try {
                outboxEventPublisher.publish(event);
                event.markPublished();
                outboxEventRepository.save(event);
                log.info("[Outbox] published: {} {}", event.getId(), event.getEventType());
            } catch (Exception e) {
                log.error("[Outbox] failed: {} {}", event.getId(), e.getMessage());
            }
        }
    }
}