package teamssavice.ssavice.s3.event;

import lombok.Builder;
import teamssavice.ssavice.imageresource.entity.ImageResource;

public class S3EventDto {

    @Builder
    public record Move(
        String sourceKey
    ) {

        public static Move from(ImageResource imageResource) {
            return Move.builder()
                .sourceKey(imageResource.getSourceKey())
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
