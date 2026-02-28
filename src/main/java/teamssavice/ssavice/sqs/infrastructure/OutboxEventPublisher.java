package teamssavice.ssavice.sqs.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import teamssavice.ssavice.outbox.entity.OutboxEvent;
import teamssavice.ssavice.sqs.event.SqsEventDto;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxEventPublisher {

    private final SqsTemplate sqsTemplate;
    private final ObjectMapper objectMapper;

    @Value("${sqs.queue.outbox}")
    private String queueUrl;

    public void publish(OutboxEvent event) {
        try {
            String messageBody = objectMapper.writeValueAsString(SqsEventDto.OutboxMessage.from(event));
            sqsTemplate.send(queueUrl, messageBody);
            log.info("[SQS] outbox event published: {} {}", event.getId(), event.getEventType());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize outbox event: " + event.getId(), e);
        }
    }
}