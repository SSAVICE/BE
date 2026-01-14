package teamssavice.ssavice.imageresource;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;
import teamssavice.ssavice.imageresource.constants.ImageContentType;
import teamssavice.ssavice.imageresource.constants.ImagePath;
import teamssavice.ssavice.imageresource.service.dto.ImageCommand;

public class ImageRequest {

    @Builder
    public record ServiceImages(
        @NotNull
        @Valid
        List<ContentType> add
    ) {
        public ImageCommand.PutPresignedUrls toCommand(Long companyId, ImagePath path) {
            List<ImageContentType> list = add.stream().map(a -> ImageContentType.from(a.contentType())).toList();

            return ImageCommand.PutPresignedUrls.builder()
                    .companyId(companyId)
                    .path(path)
                    .add(list)
                    .build();

        }
    }

    @Builder
    public record ContentType(
        @NotNull
        @Schema(
            description = "지원 MIME 타입: image/jpeg, image/png, image/webp",
            example = "image/jpeg,image/png,image/webp"
        )
        String contentType
    ) {

    }

    @Builder
    public record Confirm(
        @NotNull
        String objectKey
    ) {

    }
}
