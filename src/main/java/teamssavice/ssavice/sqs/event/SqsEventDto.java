package teamssavice.ssavice.sqs.event;

import java.time.Instant;
import lombok.Builder;
import teamssavice.ssavice.outbox.entity.OutboxEvent;

public class SqsEventDto {

    @Builder
    public record ThumbnailEvent(
        String status,
        String bucket,
        String originKey,
        String thumbKey,
        String root,
        Long ownerId,
        Instant ts
    ) {

    }

    @Builder
    public record OutboxMessage(
            Long aggregateId,
            String eventType,
            String payload
    ) {
        public static OutboxMessage from(OutboxEvent event) {
            return OutboxMessage.builder()
                    .aggregateId(event.getAggregateId())
                    .eventType(event.getEventType().name())
                    .payload(event.getPayload())
                    .build();
        }
    }



}
