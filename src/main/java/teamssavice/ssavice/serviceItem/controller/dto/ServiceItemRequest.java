package teamssavice.ssavice.serviceItem.controller.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import org.springframework.data.domain.PageRequest;
import teamssavice.ssavice.address.AddressRequest;
import teamssavice.ssavice.imageresource.ImageRequest;
import teamssavice.ssavice.s3.dto.S3Command;
import teamssavice.ssavice.serviceItem.constants.Range;
import teamssavice.ssavice.serviceItem.constants.ServiceCategory;
import teamssavice.ssavice.serviceItem.constants.SortType;
import teamssavice.ssavice.serviceItem.service.dto.ServiceItemCommand;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ServiceItemRequest {

    public record Create(
        @NotNull @PositiveOrZero Long imageCnt,
        @NotNull ServiceCategory category,
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
            List<String> objectKeys = imageConfirms.stream().map(ImageRequest.Confirm::objectKey).toList();
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
        @NotNull ServiceCategory category,
        String query,
        String gugun,
        String region,
        @NotNull Range range,
        @PositiveOrZero
        Long minPrice,
        @PositiveOrZero
        Long maxPrice,

        @NotNull SortType sortBy,

        // 커서 방식 (안드로이드 무한 스크롤과)
        @PositiveOrZero
        Long lastId,
        List<String> searchAfter,
        Boolean onSale,

        @NotNull @DecimalMin("-90") @DecimalMax("90") BigDecimal userLatitude,
        @NotNull @DecimalMin("-180") @DecimalMax("180") BigDecimal userLongitude

    ) {
        public ServiceItemCommand.Search toCommand(int size) {
            return ServiceItemCommand.Search.builder()
                .category(category)
                .query(query)
                .gugun(gugun)
                .region(region)
                .range(range)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .sortType(sortBy)
                .lastId(lastId)
                .searchAfter(searchAfter)
                .pageable(PageRequest.of(0, size))
                .onSale(Boolean.TRUE.equals(onSale))
                .userLatitude(userLatitude)
                .userLongitude(userLongitude)
                .build();
        }
    }

    @Builder
    public record Nearby(
        @NotNull @DecimalMin("-90") @DecimalMax("90") BigDecimal userLatitude,
        @NotNull @DecimalMin("-180") @DecimalMax("180") BigDecimal userLongitude,
        @NotNull @DecimalMin("-90") @DecimalMax("90") BigDecimal latitude,
        @NotNull @DecimalMin("-180") @DecimalMax("180") BigDecimal longitude,
        @NotNull @Positive @Max(100000) Integer radiusMeters,
        @PositiveOrZero Long lastId
    ) {
        public ServiceItemCommand.Nearby toCommand(int size) {
            return ServiceItemCommand.Nearby.of(
                userLatitude,
                userLongitude,
                latitude,
                longitude,
                radiusMeters,
                size,
                lastId
            );
        }
    }

}
