package teamssavice.ssavice.review.controller.dto;

import lombok.Builder;
import teamssavice.ssavice.review.service.dto.ReviewModel;

import java.time.LocalDateTime;

public class ReviewResponse {

    @Builder
    public record Item(
            String userName,
            String comment,
            String serviceName,
            LocalDateTime createdAt,
            Integer rating
    ) {
        public static ReviewResponse.Item from(ReviewModel.Item model) {
            return Item.builder()
                    .userName(model.userName())
                    .comment(model.comment())
                    .serviceName(model.serviceName())
                    .createdAt(model.createdAt())
                    .rating(model.rating())
                    .build();
        }
    }
}
