package teamssavice.ssavice.s3.event;

import lombok.Builder;
import teamssavice.ssavice.imageresource.constants.ImageContentType;
import teamssavice.ssavice.imageresource.entity.ImageResource;

public class S3EventDto {

    @Builder
    public record Move(
        Long imageResourceId,
        String sourceKey,
        String targetKey,
        ImageContentType contentType
    ) {

        public static Move from(ImageResource imageResource) {
            return Move.builder()
                .imageResourceId(imageResource.getId())
                .sourceKey(imageResource.getSourceKey())
                .targetKey(imageResource.getTargetKey())
                .contentType(ImageContentType.from(imageResource.getContentType()))
                .build();
        }
    }

    @Builder
    public record Delete(
        String targetKey
    ) {

        public static Delete from(ImageResource imageResource) {
            return Delete.builder()
                .targetKey(imageResource.getTargetKey())
                .build();
        }
    }
}
