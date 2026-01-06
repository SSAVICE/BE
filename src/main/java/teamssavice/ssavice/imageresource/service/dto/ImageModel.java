package teamssavice.ssavice.imageresource.service.dto;

import lombok.Builder;

public class ImageModel {

    @Builder
    public record PutPresignedUrl(
            String uploadUrl,
            String objectKey
    ) {
        public static PutPresignedUrl from(String uploadUrl, String objectKey) {
            return PutPresignedUrl.builder()
                    .uploadUrl(uploadUrl)
                    .objectKey(objectKey)
                    .build();
        }
    }
}
