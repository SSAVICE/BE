package teamssavice.ssavice.outbox.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import teamssavice.ssavice.global.exception.OutboxSerializeException;
import teamssavice.ssavice.outbox.constants.EventType;
import teamssavice.ssavice.outbox.entity.OutboxEvent;
import teamssavice.ssavice.outbox.infrastructure.repository.OutboxEventRepository;

@Service
@RequiredArgsConstructor
public class OutboxWriteService {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    public void saveEvent(Long aggregateId, EventType eventType, Object document) {
        try {
            outboxEventRepository.save(
                    OutboxEvent.builder()
                            .aggregateId(aggregateId)
                            .eventType(eventType)
                            .payload(objectMapper.writeValueAsString(document))
                            .build()
            );
        } catch (JsonProcessingException e) {
            throw new OutboxSerializeException();
        }
    }
}
