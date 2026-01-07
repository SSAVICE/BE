package teamssavice.ssavice.imageresource;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

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
