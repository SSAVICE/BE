package teamssavice.ssavice.s3.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import software.amazon.awssdk.services.s3.model.S3Exception;
import teamssavice.ssavice.imageresource.service.ImageService;
import teamssavice.ssavice.s3.S3Service;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3EventHandler {

    private final ImageService imageService;
    private final S3Service s3Service;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void moveImageEventListener(S3EventDto.Move event) {
        try {
            imageService.handleImageMove(event.imageResourceId(), event.sourceKey(), event.targetKey(), event.contentType());
        } catch (S3Exception e) {
            log.error("Failed to handle image move event: {}", event, e);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void deleteImageEventListener(S3EventDto.Delete event) {
        s3Service.deleteObject(event.targetKey());
    }
}
