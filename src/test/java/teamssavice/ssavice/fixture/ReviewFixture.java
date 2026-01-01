package teamssavice.ssavice.fixture;

import teamssavice.ssavice.review.entity.Review;
import teamssavice.ssavice.review.entity.UserName;

public class ReviewFixture {

    public static Review setCompany(Long companyId) {
        return Review.builder()
                .companyId(companyId)
                .rating(4)
                .comment("comment")
                .userName(UserName.of("USERNAME"))
                .serviceName("serviceName")
                .userId(1L)
                .serviceId(1L)
                .build();
    }
}
