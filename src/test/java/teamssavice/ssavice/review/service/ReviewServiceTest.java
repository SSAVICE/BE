package teamssavice.ssavice.review.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import teamssavice.ssavice.company.entity.Company;
import teamssavice.ssavice.company.infrastructure.repository.CompanyRepository;
import teamssavice.ssavice.fixture.CompanyFixture;
import teamssavice.ssavice.fixture.ServiceItemFixture;
import teamssavice.ssavice.fixture.UserFixture;
import teamssavice.ssavice.review.infrastructure.repository.ReviewRepository;
import teamssavice.ssavice.review.service.dto.ReviewCommand;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;
import teamssavice.ssavice.serviceItem.infrastructure.repository.ServiceItemRepository;
import teamssavice.ssavice.user.entity.Users;
import teamssavice.ssavice.user.infrastructure.repository.UserRepository;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
class ReviewServiceTest {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServiceItemRepository serviceItemRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    private Company company;
    private Users user;
    private ServiceItem serviceItem;

    @BeforeEach
    void setUp() {

        reviewRepository.deleteAllInBatch();
        serviceItemRepository.deleteAllInBatch();
        companyRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();

        user = userRepository.save(UserFixture.user());
        company = companyRepository.save(CompanyFixture.company(user));
        serviceItem = serviceItemRepository.save(ServiceItemFixture.base(company));
    }

    @Test
    @DisplayName("리뷰 작성 평점 관련 동시성 테스트")
    void saveReviewConcurrentlyTest() throws InterruptedException {
        // given
        int rating = 5;
        int threadCount = 100;
        // 100개 쓰레드 미리만들어두고
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for (int i = 0; i < threadCount; i++) {
            int finalI = i;
            executorService.submit(() -> {
                try {
                    ReviewCommand.Input command = new ReviewCommand.Input(
                            user.getId(),
                            company.getId(),
                            serviceItem.getId(),
                            rating,
                            "테스트 리뷰 " + finalI
                    );
                    reviewService.saveReview(command);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // then
        Company actual = companyRepository.findById(company.getId()).orElseThrow();

        assertAll(
                () -> assertThat(actual.getRatingSum()).isEqualTo(rating * threadCount),
                () -> assertThat(actual.getRateCount()).isEqualTo(threadCount)
        );
    }
}