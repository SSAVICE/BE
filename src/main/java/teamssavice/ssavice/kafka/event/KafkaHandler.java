package teamssavice.ssavice.kafka.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import teamssavice.ssavice.kafka.KafkaProducer;

@Component
@RequiredArgsConstructor
public class KafkaHandler {

    private final KafkaProducer kafkaProducer;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishEventListener(KafkaEvent.Join event) {
        kafkaProducer.publish(event.roomId(), event);
    }
}
