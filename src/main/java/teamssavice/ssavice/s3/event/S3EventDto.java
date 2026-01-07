package teamssavice.ssavice.s3.event;

import lombok.Builder;

public class S3EventDto {

    @Builder
    public record UpdateTag(
        String objectKey,
        boolean isActive
    ) {
        public static UpdateTag from(String objectKey, boolean isActive) {
            return UpdateTag.builder()
                    .objectKey(objectKey)
                    .isActive(isActive)
                    .build();
        }
    }
}
