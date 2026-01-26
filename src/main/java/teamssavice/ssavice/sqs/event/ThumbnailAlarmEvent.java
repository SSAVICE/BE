package teamssavice.ssavice.sqs.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import teamssavice.ssavice.sqs.service.ThumbnailAlarmService;

@Component
@RequiredArgsConstructor
public class ThumbnailAlarmEvent {

    private final ThumbnailAlarmService thumbnailAlarmService;

    @EventListener
    public void alarmListener(SqsEventDto.ThumbnailEvent event) {
        thumbnailAlarmService.handle(event);
    }
}
