package teamssavice.ssavice.sqs.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import teamssavice.ssavice.sqs.service.ThumbnailService;

@Component
@RequiredArgsConstructor
public class ThumbnailEventListener {

    private final ThumbnailService thumbnailService;

    @EventListener
    public void alarmListener(SqsEventDto.ThumbnailEvent event) {
        thumbnailService.metaDataEditAsConfirm(event);
    }
}
