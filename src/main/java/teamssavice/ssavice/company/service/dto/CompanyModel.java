package teamssavice.ssavice.company.service.dto;

import lombok.Builder;
import teamssavice.ssavice.auth.Token;
import teamssavice.ssavice.company.entity.Company;
import teamssavice.ssavice.review.entity.Review;
import teamssavice.ssavice.review.service.dto.ReviewModel;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;
import teamssavice.ssavice.serviceItem.service.dto.ServiceItemModel;

import java.math.BigDecimal;
import java.util.List;

public class CompanyModel {

    @Builder
    public record Login(
            String accessToken,
            Long expiresIn,
            String refreshToken,
            Boolean isRegistered
    ) {
        public static CompanyModel.Login from(Token token, boolean isRegistered) {
            return CompanyModel.Login.builder()
                    .accessToken(token.accessToken())
                    .expiresIn(token.expiresIn())
                    .refreshToken(token.refreshToken())
                    .isRegistered(isRegistered)
                    .build();
        }
    }

    @Builder
    public record MyCompany(
            Long companyId,
            String companyName,
            String phoneNumber,
            String imageUrl,
            String businessNumber,
            BigDecimal longitude,
            BigDecimal latitude,
            String postCode,
            String address,
            String detailAddress,
            String description,
            String detail,
            List<ServiceItemModel.Summary> service
    ){
        public static CompanyModel.MyCompany from(Company company, String presignedUrl, List<ServiceItem> services) {
            List<ServiceItemModel.Summary> models = services.stream().map(ServiceItemModel.Summary::from).toList();
            return CompanyModel.MyCompany.builder()
                    .companyId(company.getId())
                    .companyName(company.getCompanyName())
                    .phoneNumber(company.getPhoneNumber())
                    .imageUrl(presignedUrl)
                    .businessNumber(company.getBusinessNumber())
                    .longitude(company.getAddress().getLongitude())
                    .latitude(company.getAddress().getLatitude())
                    .postCode(company.getAddress().getPostCode())
                    .address(company.getAddress().getAddress())
                    .detailAddress(company.getAddress().getDetailAddress())
                    .description(company.getDescription())
                    .detail(company.getDetail())
                    .service(models)
                    .build();
        }
    }

    @Builder
    public record Info(
            CompanyModel.MyCompany myCompany,
            List<ReviewModel.Item> review
    ) {
        public static CompanyModel.Info from(Company company, String presignedUrl, List<ServiceItem> services, List<Review> reviews) {
            CompanyModel.MyCompany my = CompanyModel.MyCompany.from(company, presignedUrl, services);
            List<ReviewModel.Item> models = reviews.stream().map(ReviewModel.Item::from).toList();
            return Info.builder()
                    .myCompany(my)
                    .review(models)
                    .build();
        }
    }

    @Builder
    public record Summary(
            Long companyId,
            String companyName,
            String address,
            String description,
            String phoneNumber,
            String companyImageUrl,
            Float companyRate,
            Long rateCount,
            List<ReviewModel.Item> review
    ) {
        public static CompanyModel.Summary from(Company company, String presignedUrl, Float companyRate, Long rateCount, List<Review> review) {
            List<ReviewModel.Item> models = review.stream().map(ReviewModel.Item::from).toList();
            return Summary.builder()
                    .companyId(company.getId())
                    .companyName(company.getCompanyName())
                    .address(company.getAddress().getAddress())
                    .description(company.getDescription())
                    .phoneNumber(company.getPhoneNumber())
                    .companyImageUrl(presignedUrl)
                    .companyRate(companyRate)
                    .rateCount(rateCount)
                    .review(models)
                    .build();
        }
    }
}
