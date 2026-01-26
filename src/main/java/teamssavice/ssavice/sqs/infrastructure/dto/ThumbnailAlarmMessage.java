package teamssavice.ssavice.sqs.infrastructure.dto;

import java.time.Instant;

public record ThumbnailAlarmMessage(
    String status,
    String bucket,
    String originKey,
    String thumbKey,
    String root,
    String ownerId,
    Instant ts //타임라인
) {

}