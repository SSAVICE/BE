package teamssavice.ssavice.s3;

import lombok.Builder;

@Builder
public record PresignedResponse (
        String uploadUrl,
        String objectKey
) {
    public static PresignedResponse from(PresignedModel model) {
        return PresignedResponse.builder()
                .uploadUrl(model.uploadUrl())
                .objectKey(model.objectKey())
                .build();
    }
}
