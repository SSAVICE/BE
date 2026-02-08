package teamssavice.ssavice.sqs.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import teamssavice.ssavice.sqs.event.SqsEventDto;
import teamssavice.ssavice.sqs.infrastructure.dto.ThumbnailAlarmMessage;

@Slf4j
@Component
@RequiredArgsConstructor
public class ThumbnailAlarmConsumer {

    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher eventPublisher;

    @SqsListener("${sqs.queue.thumbnail-alarm}")
    public void consume(String message) {
        try {
            ThumbnailAlarmMessage alarm =
                objectMapper.readValue(message, ThumbnailAlarmMessage.class);

            SqsEventDto.ThumbnailEvent event = SqsEventDto.ThumbnailEvent.builder()
                .status(alarm.status())
                .bucket(alarm.bucket())
                .originKey(alarm.originKey())
                .thumbKey(alarm.thumbKey())
                .root(alarm.root())
                .ownerId(Long.valueOf(alarm.ownerId()))
                .ts(alarm.ts())
                .build();

            log.info("[SQS] thumbnail alarm received: {}", event);
            eventPublisher.publishEvent(event);

        } catch (Exception e) {
            log.error("[SQS] failed to consume message: {}", message, e);
            // TODO 디스코드 알람
        }
    }
}
