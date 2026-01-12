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
            String description,
            String depositor,
            String accountNumber,
            String detail,
            String regionCode,
            String postCode,
            String address,
            String detailAddress,
            BigDecimal longitude,
            BigDecimal latitude
    ) {
        public static CompanyCommand.Create from(Long userId, CompanyRequest.Create request) {
            return Create.builder()
                    .userId(userId)
                    .companyName(request.companyName())
                    .ownerName(request.ownerName())
                    .phoneNumber(request.phoneNumber())
                    .businessNumber(request.businessNumber())
                    .accountNumber(request.accountNumber())
                    .depositor(request.depositor())
                    .description(request.description())
                    .detail(request.detail())
                    .regionCode(request.region().regionCode())
                    .postCode(request.region().postCode())
                    .address(request.region().address())
                    .detailAddress(request.region().detailAddress())
                    .longitude(request.region().longitude())
                    .latitude(request.region().latitude())
                    .build();
        }
    }

    @Builder
    public record Update(
            Long companyId,
            String companyName,
            String ownerName,
            String phoneNumber,
            String description,
            String depositor,
            String accountNumber,
            String detail,
            String regionCode,
            String postCode,
            String address,
            String detailAddress,
            BigDecimal longitude,
            BigDecimal latitude
    ) {
        public static CompanyCommand.Update from(Long companyId, CompanyRequest.Update request) {
            return Update.builder()
                    .companyId(companyId)
                    .companyName(request.companyName())
                    .ownerName(request.ownerName())
                    .phoneNumber(request.phoneNumber())
                    .accountNumber(request.accountNumber())
                    .depositor(request.depositor())
                    .description(request.description())
                    .detail(request.detail())
                    .regionCode(request.region().regionCode())
                    .postCode(request.region().postCode())
                    .address(request.region().address())
                    .detailAddress(request.region().detailAddress())
                    .longitude(request.region().longitude())
                    .latitude(request.region().latitude())
                    .build();
        }
    }

    @Builder
    public record Validate(
            String businessNumber,
            String startDate,
            String name
    ){
        public static Validate from(CompanyRequest.Validate request) {
            return Validate.builder()
                    .businessNumber(request.businessNumber())
                    .startDate(request.startDate())
                    .name(request.name())
                    .build();
        }
    }
}
