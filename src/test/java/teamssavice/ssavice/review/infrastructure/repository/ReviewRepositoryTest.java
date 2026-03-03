package teamssavice.ssavice.review.infrastructure.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.annotation.DirtiesContext;
import teamssavice.ssavice.account.entity.Account;
import teamssavice.ssavice.company.entity.Company;
import teamssavice.ssavice.fixture.CompanyFixture;
import teamssavice.ssavice.fixture.ReviewFixture;
import teamssavice.ssavice.fixture.ServiceItemFixture;
import teamssavice.ssavice.fixture.UserFixture;
import teamssavice.ssavice.global.config.QueryDSLConfig;
import teamssavice.ssavice.review.entity.Review;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;
import teamssavice.ssavice.user.entity.Users;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
@EnableJpaAuditing
@Import(QueryDSLConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ReviewRepositoryTest {
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    TestEntityManager tem;

    private final List<Review> reviews = new ArrayList<>();
    private Company company;
    private Users user;

    @BeforeEach
    void setUp() {
        Account account = UserFixture.account();
        tem.persist(account);
        user = UserFixture.user(account);
        tem.persist(user);

        Account companyAccount = CompanyFixture.account();
        tem.persist(companyAccount);
        company = CompanyFixture.company(companyAccount);
        tem.persist(company);

        ServiceItem item = ServiceItemFixture.base(company);
        tem.persist(item);

        for (int i = 0; i < 10; i++) {
            reviews.add(ReviewFixture.setUserAndCompanyAndServiceItem(user, company, item));
        }
    }

    @Test
    @DisplayName("Company로 리뷰 페이징 조회 테스트")
    void findByCompanyIdPagingTest() {
        // given
        reviewRepository.saveAll(reviews);
        Long companyId = this.company.getId();
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        tem.flush();
        tem.clear();

        // when
        Page<Review> actuals = reviewRepository.findByCompanyId(companyId, pageable);

        // then
        assertThat(actuals.getContent().get(0).getCreatedAt()).isAfterOrEqualTo(actuals.getContent().get(9).getCreatedAt());
    }

    @Test
    @DisplayName("companyId로 최신 Review 3개 조회 테스트")
    void findTop3ByCompanyIdOrderByCreatedAtDescTest() {
        // given
        reviewRepository.saveAll(reviews);
        Long companyId = this.company.getId();
        tem.flush();
        tem.clear();

        // when
        List<Review> actuals = reviewRepository.findTop3ByCompanyIdOrderByCreatedAtDesc(companyId, PageRequest.of(0, 3));

        // then
        assertAll(
                () -> assertThat(actuals).hasSize(3),
                () -> assertThat(actuals.get(0).getCreatedAt()).isAfterOrEqualTo(actuals.get(1).getCreatedAt()),
                () -> assertThat(actuals.get(1).getCreatedAt()).isAfterOrEqualTo(actuals.get(2).getCreatedAt())
        );
    }

    @Test
    @DisplayName("User로 리뷰 페이징 조회 테스트")
    void findByUserIdPagingTest() {
        // given
        reviewRepository.saveAll(reviews);
        Long userId = this.user.getId();
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        tem.flush();
        tem.clear();

        // when
        Page<Review> actuals = reviewRepository.findByUser_Id(userId, pageable);

        // then
        assertThat(actuals.getContent().get(0).getCreatedAt()).isAfterOrEqualTo(actuals.getContent().get(9).getCreatedAt());
    }
}
