package teamssavice.ssavice.review.infrastructure.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.annotation.DirtiesContext;
import teamssavice.ssavice.fixture.ReviewFixture;
import teamssavice.ssavice.review.entity.Review;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@EnableJpaAuditing
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ReviewRepositoryTest {
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    EntityManager em;

    private final List<Review> reviews = new ArrayList<>();
    private final Long companyId = 1L;

    @BeforeEach
    void setUp() {
        for (int i = 0; i < 10; i++) {
            reviews.add(ReviewFixture.setCompany(companyId));
        }
    }

    @Test
    @DisplayName("리뷰 페이징 조회 테스트")
    void findByCompanyIdPagingTest() {
        // given
        reviewRepository.saveAll(reviews);
        Long companyId = this.companyId;
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        em.flush();
        em.clear();

        // when
        Page<Review> actuals = reviewRepository.findByCompanyId(companyId, pageable);

        // then
        assertThat(actuals.getContent().get(0).getCreatedAt()).isAfterOrEqualTo(actuals.getContent().get(9).getCreatedAt());
    }

}