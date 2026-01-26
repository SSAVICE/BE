package teamssavice.ssavice.sqs.event;

import java.time.Instant;
import lombok.Builder;

public class SqsEventDto {

    @Builder
    public record ThumbnailEvent(
        String status,
        String bucket,
        String originKey,
        String thumbKey,
        String root,
        Long ownerId,
        Instant ts
    ) {

    }

}
