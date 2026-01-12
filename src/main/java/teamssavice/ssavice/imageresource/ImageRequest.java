package teamssavice.ssavice.imageresource;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;

public class ImageRequest {

    @Builder
    public record ServiceImages(
        @NotNull
        @Valid
        List<ContentType> add
    ) {

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
