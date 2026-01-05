package teamssavice.ssavice.s3.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import teamssavice.ssavice.s3.S3Service;

@Component
@RequiredArgsConstructor
public class S3EventHandler {
    private final S3Service s3Service;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void updateImageTagEventListener(S3EventDto.UpdateTag event) {
        s3Service.updateIsActiveTag(event.objectKey(), event.isActive());
    }
}
