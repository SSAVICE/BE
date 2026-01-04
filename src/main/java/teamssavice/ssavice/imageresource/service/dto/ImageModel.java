package teamssavice.ssavice.imageresource.service.dto;

import lombok.Builder;

public class ImageModel {

    @Builder
    public record PutPresigned(
            String uploadUrl,
            String objectKey
    ) {
        public static PutPresigned from(String uploadUrl, String objectKey) {
            return PutPresigned.builder()
                    .uploadUrl(uploadUrl)
                    .objectKey(objectKey)
                    .build();
        }
    }
}
