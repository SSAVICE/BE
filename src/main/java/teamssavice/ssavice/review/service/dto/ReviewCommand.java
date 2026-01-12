package teamssavice.ssavice.review.service.dto;

import lombok.Builder;

public class ReviewCommand {

    @Builder
    public record Input(
            Long userId,
            Long companyId,
            Long serviceId,
            Integer rating,
            String comment
    ) {
    }
}
