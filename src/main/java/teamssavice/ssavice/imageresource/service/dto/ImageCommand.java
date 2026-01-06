package teamssavice.ssavice.imageresource.service.dto;

import lombok.Builder;
import teamssavice.ssavice.imageresource.ImageRequest;
import teamssavice.ssavice.imageresource.constants.ImageContentType;
import teamssavice.ssavice.imageresource.constants.ImagePath;

import java.util.List;

public class ImageCommand {

    @Builder
    public record PutPresignedUrls(
            Long companyId,
            ImagePath path,
            List<ImageContentType> add
    ) {
        public static ImageCommand.PutPresignedUrls from(Long companyId, ImagePath path, ImageRequest.ServiceImages list) {
            List<ImageContentType> add = list.add().stream().map(a -> ImageContentType.from(a.contentType())).toList();

            return PutPresignedUrls.builder()
                    .companyId(companyId)
                    .path(path)
                    .add(add)
                    .build();
        }
    }
}
