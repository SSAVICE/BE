package teamssavice.ssavice.imageresource;

import lombok.Builder;
import teamssavice.ssavice.imageresource.service.dto.ImageModel;

public class ImageResponse {

    @Builder
    public record Presigned(
            String uploadUrl,
            String objectKey
    ) {
        public static ImageResponse.Presigned from(ImageModel.PutPresigned model) {
            return Presigned.builder()
                    .uploadUrl(model.uploadUrl())
                    .objectKey(model.objectKey())
                    .build();
        }
    }
}
