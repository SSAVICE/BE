package teamssavice.ssavice.serviceItem.controller.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
            String detailAddress // 상세주소를 필수로 할 것이냐에 따라서



    ) {
    }
}
