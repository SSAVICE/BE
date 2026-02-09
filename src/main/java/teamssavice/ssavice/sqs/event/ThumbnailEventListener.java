package teamssavice.ssavice.sqs.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import teamssavice.ssavice.imageresource.service.ImageService;
import teamssavice.ssavice.sqs.constants.SqsMessageStatus;

@Slf4j
@Component
@RequiredArgsConstructor
public class ThumbnailEventListener {

    private final ImageService imageService;

    @EventListener
    public void alarmListener(SqsEventDto.ThumbnailEvent event) {
        if (!event.status().equals(SqsMessageStatus.DONE.name())) {
            log.warn("thumbnail not completed: {}", event);
            return;
        }
        imageService.changeMetaDataToThumbnail(event.originKey(), event.thumbKey());
    }
}
