package teamssavice.ssavice.review.service.dto;

import lombok.Builder;
import teamssavice.ssavice.review.entity.Review;

import java.time.LocalDateTime;

public class ReviewModel {

    @Builder
    public record Item(
            Long userId,
            String userName,
            String comment,
            String serviceId,
            String serviceName,
            LocalDateTime createdAt,
            Integer rating
    ) {
        public static ReviewModel.Item from(Review review) {
            return Item.builder()
                    .userId(review.getId())
                    .userName(review.getUser().getName())
                    .comment(review.getComment())
                    .serviceName(review.getServiceItem().getTitle())
                    .createdAt(review.getCreatedAt())
                    .rating(review.getRating())
                    .build();
        }
    }
}
