package teamssavice.ssavice.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.review.entity.Review;
import teamssavice.ssavice.review.infrastructure.repository.ReviewRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewReadService {

    private final ReviewRepository reviewRepository;

    @Transactional(readOnly = true)
    public Page<Review> findByCompanyIdPaging(Long companyId, Pageable pageable) {
        return reviewRepository.findByCompanyId(companyId, pageable);
    }

    @Transactional(readOnly = true)
    public List<Review> findTop3ByCompanyIdOrderByCreatedAt(Long companyId) {
        return reviewRepository.findTop3ByCompanyIdOrderByCreatedAtDesc(companyId);
    }
}
