package teamssavice.ssavice.book.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import teamssavice.ssavice.outbox.constants.EventType;
import teamssavice.ssavice.outbox.service.OutboxWriteService;

@Component
@RequiredArgsConstructor
public class BookEventHandler {

    private final OutboxWriteService outboxWriteService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleBookApplied(BookChangedEvent event) {
        outboxWriteService.saveEvent(
                event.serviceItemId(),
                EventType.CREATED,
                event.document()
        );
    }
}