package teamssavice.ssavice.serviceItem.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import teamssavice.ssavice.outbox.constants.EventType;
import teamssavice.ssavice.outbox.service.OutboxWriteService;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ServiceItemOutboxEventListener {

    private final OutboxWriteService outboxWriteService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleServiceItemCreated(ServiceItemCreatedEvent event) {
        outboxWriteService.saveEvent(
                event.serviceItemId(),
                EventType.CREATED,
                event.document()
        );
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleServiceItemDeleted(ServiceItemDeletedEvent event) {
        outboxWriteService.saveEvent(
                event.serviceItemId(),
                EventType.DELETED,
                Map.of("id", event.serviceItemId())
        );
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleServiceItemThumbnailUpdated(ServiceItemThumbnailUpdatedEvent event) {
        outboxWriteService.saveEvent(
                event.serviceItemId(),
                EventType.THUMBNAIL_UPDATED,
                Map.of("thumbnailObjectKey", event.thumbKey())
        );
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleServiceItemAvailabilityChanged(ServiceItemAvailabilityChangedEvent event) {
        outboxWriteService.saveEvent(
                event.serviceItemId(),
                EventType.AVAILABILITY_UPDATED,
                Map.of("isAvailable", event.isAvailable())
        );
    }
}