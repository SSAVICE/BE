package teamssavice.ssavice.review.service.dto;

import lombok.Builder;
import teamssavice.ssavice.review.controller.dto.ReviewRequest;

public class ReviewCommand {

    @Builder
    public record Input(
            Long userId,
            Long companyId,
            Long serviceId,
            Integer rating,
            String comment
    ) {
        public static ReviewCommand.Input from(Long userId, ReviewRequest.Input request) {
            return Input.builder()
                    .userId(userId)
                    .companyId(request.companyId())
                    .serviceId(request.serviceId())
                    .rating(request.rating())
                    .comment(request.comment())
                    .build();
        }
    }
}
