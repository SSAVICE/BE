package teamssavice.ssavice.serviceItem.controller.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import teamssavice.ssavice.imageresource.ImageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
            @NotNull BigDecimal longitude,
            @NotNull BigDecimal latitude,
            @NotBlank String postCode,
            @NotBlank String address,
            String detailAddress, // 상세주소를 필수로 할 것이냐에 따라서
            @NotNull List<ImageRequest.Confirm> imageConfirms
    ) {
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Search {

        private String category;
        private String query;

        private String region1;
        private String region2;

        private Integer range;

        @PositiveOrZero
        private Long minPrice;
        @PositiveOrZero
        private Long maxPrice;

        private Integer sortBy;

        // 커서 방식 (안드로이드 무한 스크롤과)
        @PositiveOrZero
        private Long lastId;
    }
}
