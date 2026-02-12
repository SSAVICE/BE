package teamssavice.ssavice.review.controller.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import teamssavice.ssavice.review.service.dto.ReviewCommand;

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
            @Min(1) @Max(5)
            Integer rating,
            @NotNull
            String comment
    ) {
        public ReviewCommand.Input toCommand(Long userId) {
            return ReviewCommand.Input.builder()
                    .userId(userId)
                    .companyId(companyId)
                    .serviceId(serviceId)
                    .rating(rating)
                    .comment(comment)
                    .build();
        }
    }
}
