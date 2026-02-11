package teamssavice.ssavice.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.company.entity.Company;
import teamssavice.ssavice.company.service.CompanyReadService;
import teamssavice.ssavice.company.service.CompanyWriteService;
import teamssavice.ssavice.review.service.dto.ReviewCommand;
import teamssavice.ssavice.review.service.dto.ReviewModel;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;
import teamssavice.ssavice.serviceItem.service.ServiceItemReadService;
import teamssavice.ssavice.user.entity.Users;
import teamssavice.ssavice.user.service.UserReadService;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewWriteService reviewWriteService;
    private final UserReadService userReadService;
    private final ServiceItemReadService serviceItemReadService;
    private final ReviewReadService reviewReadService;
    private final CompanyWriteService companyWriteService;

    @Transactional
    public void saveReview(ReviewCommand.Input command) {
        Users user = userReadService.findById(command.userId());
        ServiceItem item = serviceItemReadService.findById(command.serviceId());

        companyWriteService.addRating(command.companyId(), command.rating());
        reviewWriteService.save(user.getName(), item.getTitle(), command);
    }

    @Transactional(readOnly = true)
    public Page<ReviewModel.Item> getReviewPaging(ReviewCommand.RetrieveByCompanyId command) {
        return reviewReadService.findByCompanyIdPaging(command.companyId(), command.pageable())
                .map(ReviewModel.Item::from);
    }
}
