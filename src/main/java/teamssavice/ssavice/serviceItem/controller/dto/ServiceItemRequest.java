package teamssavice.ssavice.serviceItem.controller.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import org.springframework.data.domain.Pageable;
import teamssavice.ssavice.address.AddressRequest;
import teamssavice.ssavice.imageresource.ImageRequest;
import teamssavice.ssavice.s3.dto.S3Command;
import teamssavice.ssavice.serviceItem.service.dto.ServiceItemCommand;

public class ServiceItemRequest {

    public record Create(
        @NotNull @PositiveOrZero Long imageCnt,
        @NotBlank String category,
        @NotBlank String title,
        @NotBlank String description,
        @NotNull @Min(1) Long minimumMember,
        @NotNull @Min(1) Long maximumMember,
        @NotNull @PositiveOrZero Long basePrice,
        @NotNull @PositiveOrZero Integer discountRate,
        @NotNull @PositiveOrZero Long discountedPrice,
        @NotNull @FutureOrPresent LocalDateTime deadline,
        String tag, // 엘라스틱 서치 적용하면서 리팩토링 예정
        @NotNull LocalDateTime startDate,
        @NotNull LocalDateTime endDate,
        @NotNull AddressRequest.Region region,
        @NotNull List<ImageRequest.Confirm> imageConfirms
    ) {

        public ServiceItemCommand.Create toCommand(Long companyId) {
            List<String> objectKeys = imageConfirms.stream().map(ImageRequest.Confirm::objectKey)
                .toList();
            return ServiceItemCommand.Create.builder()
                .companyId(companyId)
                .title(title)
                .description(description)
                .basePrice(basePrice)
                .discountRate(discountRate)
                .minimumMember(minimumMember)
                .maximumMember(maximumMember)
                .startDate(startDate)
                .endDate(endDate)
                .deadline(deadline)
                .category(category)
                .tag(tag)
                .regionCode(region().regionCode())
                .longitude(region().longitude())
                .latitude(region().latitude())
                .postCode(region().postCode())
                .address(region().address())
                .detailAddress(region().detailAddress())
                .imageObjectKeys(objectKeys)
                .build();
        }

        public S3Command.ValidateKeys toValidateCommand() {
            return S3Command.ValidateKeys.builder()
                .objectKeys(
                    imageConfirms.stream().map(ImageRequest.Confirm::objectKey).toList())
                .build();
        }
    }

    @Builder
    public record Search(
        String category,
        String query,
        String gugun,
        String region,
        Integer range,
        @PositiveOrZero
        Long minPrice,
        @PositiveOrZero
        Long maxPrice,

        Integer sortBy,

        // 커서 방식 (안드로이드 무한 스크롤과)
        @PositiveOrZero
        Long lastId,

        Boolean onSale
    ) {

        public ServiceItemCommand.Search toCommand(Pageable pageable) {
            return ServiceItemCommand.Search.builder()
                .category(category)
                .query(query)
                .gugun(gugun)
                .region(region)
                .range(range)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .sortBy(sortBy)
                .lastId(lastId)
                .pageable(pageable)
                .onSale(Boolean.TRUE.equals(onSale))
                .build();
        }
    }

}
