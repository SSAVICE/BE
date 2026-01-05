package teamssavice.ssavice.imageresource;

import lombok.Builder;
import teamssavice.ssavice.imageresource.service.dto.ImageModel;

import java.util.List;

public class ImageResponse {

    @Builder
    public record PresignedUrls(
            List<PresignedUrl> list
    ) {
        public static ImageResponse.PresignedUrls from(List<ImageModel.PutPresignedUrl> models) {
            List<PresignedUrl> list = models.stream().map(PresignedUrl::from).toList();
            return PresignedUrls.builder()
                    .list(list)
                    .build();
        }
    }

    @Builder
    public record PresignedUrl(
            String uploadUrl,
            String objectKey
    ) {
        public static ImageResponse.PresignedUrl from(ImageModel.PutPresignedUrl model) {
            return PresignedUrl.builder()
                    .uploadUrl(model.uploadUrl())
                    .objectKey(model.objectKey())
                    .build();
        }
    }
}
