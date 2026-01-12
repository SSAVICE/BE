package teamssavice.ssavice.imageresource.service.dto;

import lombok.Builder;
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
    }
}
