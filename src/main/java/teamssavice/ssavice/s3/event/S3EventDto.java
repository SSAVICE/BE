package teamssavice.ssavice.s3.event;

import lombok.Builder;
import teamssavice.ssavice.imageresource.constants.ImageContentType;
import teamssavice.ssavice.imageresource.entity.ImageResource;

public class S3EventDto {

    @Builder
    public record Move(
        String sourceKey,
        String targetKey,
        ImageContentType contentType
    ) {
        public static Move from(ImageResource imageResource) {
            return Move.builder()
                    .sourceKey(imageResource.getTempKey())
                    .targetKey(imageResource.getObjectKey())
                    .contentType(ImageContentType.from(imageResource.getContentType()))
                    .build();
        }
    }

    @Builder
    public record Delete(
            String objectKey
    ) {
        public static Delete from(ImageResource imageResource) {
            return Delete.builder()
                    .objectKey(imageResource.getObjectKey())
                    .build();
        }
    }
}
