package teamssavice.ssavice.s3;

import jakarta.validation.constraints.NotNull;

public record PresignedRequest (
        @NotNull
        String contentType
){
}
