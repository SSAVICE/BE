package teamssavice.ssavice.s3;

import lombok.Builder;

@Builder
public record PresignedModel(
        String uploadUrl,
        String objectKey
) {
    public static PresignedModel from(String uploadUrl, String objectKey) {
        return PresignedModel.builder()
                .uploadUrl(uploadUrl)
                .objectKey(objectKey)
                .build();
    }
}
