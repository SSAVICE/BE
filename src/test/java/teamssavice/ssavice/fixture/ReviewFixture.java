package teamssavice.ssavice.fixture;

import teamssavice.ssavice.company.entity.Company;
import teamssavice.ssavice.review.entity.Review;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;
import teamssavice.ssavice.user.entity.Users;

public class ReviewFixture {

    public static Review setUserAndCompanyAndServiceItem(Users user, Company company, ServiceItem serviceItem) {
        return Review.builder()
                .companyId(company.getId())
                .user(user)
                .serviceItem(serviceItem)
                .rating(4)
                .comment("comment")
                .build();
    }
}
