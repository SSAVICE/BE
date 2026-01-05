package teamssavice.ssavice.imageresource;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

public class ImageRequest {

    @Builder
    public record ContentType(
            @NotNull
            String contentType
    ) {
    }

    @Builder
    public record Confirm(
            @NotNull
            String objectKey
    ){
    }
}
