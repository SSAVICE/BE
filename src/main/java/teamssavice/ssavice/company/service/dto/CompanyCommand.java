package teamssavice.ssavice.company.service.dto;

import java.math.BigDecimal;
import lombok.Builder;
import teamssavice.ssavice.company.controller.dto.CompanyRequest;
import teamssavice.ssavice.company.infrastructure.dto.CompanyInfraCommand;

public class CompanyCommand {

    @Builder
    public record Create(
        Long userId,
        String verifyToken,
        String companyName,
        String ownerName,
        String phoneNumber,
        String businessNumber,
        String description,
        String depositor,
        String accountNumber,
        String detail,
        String postCode,
        String address,
        String detailAddress,
        BigDecimal longitude,
        BigDecimal latitude
    ) {

        public static CompanyCommand.Create from(Long userId, CompanyRequest.Create request) {
            return Create.builder()
                .userId(userId)
                .verifyToken(request.verifyToken())
                .companyName(request.companyName())
                .ownerName(request.ownerName())
                .phoneNumber(request.phoneNumber())
                .businessNumber(request.businessNumber())
                .accountNumber(request.accountNumber())
                .depositor(request.depositor())
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
                .postCode(request.postCode())
                .address(request.address())
                .detailAddress(request.detailAddress())
                .longitude(request.longitude())
                .latitude(request.latitude())
                .build();
        }
    }

    @Builder
    public record Validate(
        String businessNumber,
        String startDate,
        String name
    ) {

        public CompanyInfraCommand.Validate toInfraCommand() {
            return CompanyInfraCommand.Validate.builder()
                .businessNumber(this.businessNumber)
                .startDate(this.startDate)
                .name(this.name)
                .build();
        }

    }
}
