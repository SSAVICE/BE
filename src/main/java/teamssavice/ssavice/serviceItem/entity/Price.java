package teamssavice.ssavice.serviceItem.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Price {

    @NotNull
    @Column(nullable = false)
    private Long basePrice;

    @NotNull
    @Column(nullable = false)
    private Integer discountRate;

    @NotNull
    @Column(nullable = false)
    private Long discountedPrice;

    public static Price of(Long basePrice, Integer discountRate) {

        Integer rate = (discountRate == null) ? 0 : discountRate;
        Long discountedPrice = calculate(basePrice, rate);

        return new Price(basePrice, rate, discountedPrice);
    }

    private static Long calculate(Long basePrice, Integer rate) {
        return Math.round(basePrice * (100 - rate) / 100.0);
    }



}
