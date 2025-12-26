package teamssavice.ssavice.company.service.dto;

import lombok.Builder;
import teamssavice.ssavice.auth.Token;
import teamssavice.ssavice.company.entity.Company;
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
        public static CompanyModel.MyCompany from(Company company, List<ServiceItem> services) {
            List<ServiceItemModel.Summary> models = services.stream().map(ServiceItemModel.Summary::from).toList();
            return CompanyModel.MyCompany.builder()
                    .companyId(company.getId())
                    .companyName(company.getCompanyName())
                    .phoneNumber(company.getPhoneNumber())
                    .imageUrl(company.getImageUrl())
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

}
