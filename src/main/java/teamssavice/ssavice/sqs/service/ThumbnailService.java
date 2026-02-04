package teamssavice.ssavice.sqs.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.imageresource.entity.ImageResource;
import teamssavice.ssavice.imageresource.service.ImageReadService;
import teamssavice.ssavice.sqs.event.SqsEventDto.ThumbnailEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class ThumbnailService {

    private final ImageReadService imageReadService;

    @Transactional
    public void metaDataEditAsConfirm(ThumbnailEvent alarm) {
        if (!"DONE".equals(alarm.status())) {
            log.warn("thumbnail not completed: {}", alarm);
            return;
        }
        ImageResource imageResource = imageReadService.findByTargetKey(alarm.originKey());
        imageResource.confirmAsThumbnail(alarm.resultKey());
    }
}