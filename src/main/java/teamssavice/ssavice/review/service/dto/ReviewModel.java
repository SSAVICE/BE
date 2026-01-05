package teamssavice.ssavice.review.service.dto;

import lombok.Builder;
import teamssavice.ssavice.review.entity.Review;

import java.time.LocalDateTime;

public class ReviewModel {

    @Builder
    public record Item(
            String userName,
            String comment,
            String serviceName,
            LocalDateTime createdAt,
            Integer rating
    ) {
        public static ReviewModel.Item from(Review review) {
            return Item.builder()
                    .userName(review.getUserName().masked())
                    .comment(review.getComment())
                    .serviceName(review.getServiceName())
                    .createdAt(review.getCreatedAt())
                    .rating(review.getRating())
                    .build();
        }
    }
}
