package teamssavice.ssavice.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.review.entity.Review;
import teamssavice.ssavice.review.infrastructure.repository.ReviewRepository;

@Service
@RequiredArgsConstructor
public class ReviewReadService {

    private final ReviewRepository reviewRepository;

    @Transactional(readOnly = true)
    public Page<Review> findByCompanyIdPaging(Long companyId, Pageable pageable) {
        return reviewRepository.findByCompanyId(companyId, pageable);
    }
}
