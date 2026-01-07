package teamssavice.ssavice.company.controller.dto;

import lombok.Builder;
import teamssavice.ssavice.company.service.dto.CompanyModel;
import teamssavice.ssavice.review.controller.dto.ReviewResponse;
import teamssavice.ssavice.serviceItem.controller.dto.ServiceItemResponse;

import java.math.BigDecimal;
import java.util.List;

public class CompanyResponse {

    @Builder
    public record Login(
            String accessToken,
            Long expiresIn,
            String refreshToken,
            Boolean isRegistered
    ) {
        public static CompanyResponse.Login from(CompanyModel.Login model) {
            return Login.builder()
                    .accessToken(model.accessToken())
                    .expiresIn(model.expiresIn())
                    .refreshToken(model.refreshToken())
                    .isRegistered(model.isRegistered())
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
            List<ServiceItemResponse.Summary> service
    ) {
        public static CompanyResponse.MyCompany from(CompanyModel.MyCompany model) {
            List<ServiceItemResponse.Summary> services = model.service().stream().map(ServiceItemResponse.Summary::from).toList();
            return MyCompany.builder()
                    .companyId(model.companyId())
                    .companyName(model.companyName())
                    .phoneNumber(model.phoneNumber())
                    .imageUrl(model.imageUrl())
                    .businessNumber(model.businessNumber())
                    .longitude(model.longitude())
                    .latitude(model.latitude())
                    .postCode(model.postCode())
                    .address(model.address())
                    .detailAddress(model.detailAddress())
                    .description(model.description())
                    .detail(model.detail())
                    .service(services)
                    .build();
        }
    }

    @Builder
    public record Info(
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
            List<ServiceItemResponse.Summary> service,
            List<ReviewResponse.Item> review
    ) {
        public static CompanyResponse.Info from(CompanyModel.Info model) {
            List<ServiceItemResponse.Summary> services = model.myCompany().service().stream().map(ServiceItemResponse.Summary::from).toList();
            List<ReviewResponse.Item> reviews = model.review().stream().map(ReviewResponse.Item::from).toList();
            return Info.builder()
                    .companyId(model.myCompany().companyId())
                    .companyName(model.myCompany().companyName())
                    .phoneNumber(model.myCompany().phoneNumber())
                    .imageUrl(model.myCompany().imageUrl())
                    .businessNumber(model.myCompany().businessNumber())
                    .longitude(model.myCompany().longitude())
                    .latitude(model.myCompany().latitude())
                    .postCode(model.myCompany().postCode())
                    .address(model.myCompany().address())
                    .detailAddress(model.myCompany().detailAddress())
                    .description(model.myCompany().description())
                    .detail(model.myCompany().detail())
                    .service(services)
                    .review(reviews)
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
            List<ReviewResponse.Item> review
    ) {
        public static CompanyResponse.Summary from(CompanyModel.Summary model) {
            List<ReviewResponse.Item> reviews = model.review().stream().map(ReviewResponse.Item::from).toList();
            return Summary.builder()
                    .companyId(model.companyId())
                    .companyName(model.companyName())
                    .address(model.address())
                    .description(model.description())
                    .phoneNumber(model.phoneNumber())
                    .companyImageUrl(model.companyImageUrl())
                    .companyRate(model.companyRate())
                    .rateCount(model.rateCount())
                    .review(reviews)
                    .build();
        }
    }

    @Builder
    public record Validate(
            boolean isValid,
            String statusMessage
    ) {
        public static CompanyResponse.Validate from(CompanyModel.Validate model) {
            return Validate.builder()
                    .isValid(model.isValid())
                    .statusMessage(model.statusMessage())
                    .build();
        }
    }
}
