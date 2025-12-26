package teamssavice.ssavice.serviceItem;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import teamssavice.ssavice.serviceItem.entity.Price;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class PriceTest {

    @Test
    @DisplayName("정가와 할인율을 입력하면 할인된 가격이 계산되어야 한다")
    void price_calculation_test() {
        // given
        Long basePrice = 10000L;
        Integer discountRate = 10;

        // when
        Price price = Price.of(basePrice, discountRate);

        // then
        // $10,000 \times (100 - 10) / 100 = 9,000$
        assertThat(price.getDiscountedPrice()).isEqualTo(9000L);
    }
}