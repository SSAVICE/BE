package teamssavice.ssavice.company.service.dto;

import lombok.Builder;
import teamssavice.ssavice.company.controller.dto.CompanyRequest;

import java.math.BigDecimal;

public class CompanyCommand {

    @Builder
    public record Create(
            Long userId,
            String companyName,
            String ownerName,
            String phoneNumber,
            String businessNumber,
            String accountNumber,
            String description,
            String detail,
            String postCode,
            String address,
            String detailAddress,
            BigDecimal longitude,
            BigDecimal latitude
    ) {
        public static CompanyCommand.Create from(Long userId, CompanyRequest.Create request) {
            return CompanyCommand.Create.builder()
                    .userId(userId)
                    .companyName(request.companyName())
                    .ownerName(request.ownerName())
                    .phoneNumber(request.phoneNumber())
                    .businessNumber(request.businessNumber())
                    .accountNumber(request.accountNumber())
                    .description(request.description())
                    .detail(request.detail())
                    .postCode(request.postCode())
                    .address(request.address())
                    .detailAddress(request.detailAddress())
                    .longitude(request.longitude())
                    .latitude(request.latitude())
                    .build();
        }
    }
}
