package teamssavice.ssavice.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.review.entity.Review;
import teamssavice.ssavice.review.infrastructure.repository.ReviewRepository;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;
import teamssavice.ssavice.user.entity.Users;

@Service
@RequiredArgsConstructor
public class ReviewWriteService {

    private final ReviewRepository reviewRepository;

    @Transactional(readOnly = true)
    public void save(Long companyId, Users user, ServiceItem serviceItem, int rating, String comment) {
        Review review = Review.builder()
                .companyId(companyId)
                .rating(rating)
                .comment(comment)
                .user(user)
                .serviceItem(serviceItem)
                .build();
        reviewRepository.save(review);
    }
}
