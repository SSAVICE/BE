package teamssavice.ssavice.review.controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

public class ReviewRequest {

    @Builder
    public record Input(
            @NotNull
            @Min(1)
            Long companyId,
            @NotNull
            @Min(1)
            Long serviceId,
            @NotNull
            @Min(0)
            Integer rating,
            @NotNull
            String comment
    ) {
    }
}
