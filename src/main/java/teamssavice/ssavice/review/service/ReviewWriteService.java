package teamssavice.ssavice.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.review.entity.Review;
import teamssavice.ssavice.review.entity.UserName;
import teamssavice.ssavice.review.infrastructure.repository.ReviewRepository;
import teamssavice.ssavice.review.service.dto.ReviewCommand;

@Service
@RequiredArgsConstructor
public class ReviewWriteService {

    private final ReviewRepository reviewRepository;

    @Transactional(readOnly = true)
    public void save(String userName, String serviceName, ReviewCommand.Input command) {
        Review review = Review.builder()
                .companyId(command.companyId())
                .rating(command.rating())
                .comment(command.comment())
                .userName(UserName.of(userName))
                .serviceName(serviceName)
                .userId(command.userId())
                .serviceId(command.serviceId())
                .build();
        reviewRepository.save(review);
    }
}
