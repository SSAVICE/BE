package teamssavice.ssavice.company.controller.dto;

import lombok.Builder;
import teamssavice.ssavice.company.service.dto.CompanyModel;
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
            List<ServiceItemResponse.Summary> responses = model.service().stream().map(ServiceItemResponse.Summary::from).toList();
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
                    .service(responses)
                    .build();
        }
    }
}
