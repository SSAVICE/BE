package teamssavice.ssavice.imageresource.service.dto;

import lombok.Builder;

public class ImageModel {

    @Builder
    public record Presigned(
            String uploadUrl,
            String objectKey
    ) {
        public static ImageModel.Presigned from(String uploadUrl, String objectKey) {
            return Presigned.builder()
                    .uploadUrl(uploadUrl)
                    .objectKey(objectKey)
                    .build();
        }
    }
}
