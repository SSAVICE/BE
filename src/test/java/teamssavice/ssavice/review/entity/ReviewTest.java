package teamssavice.ssavice.review.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReviewTest {

    @Test
    @DisplayName("Review UserName Embedded 테스트")
    void reviewUserNameEmbeddedTest() {
        // given
        Review review = Review.builder().userName(UserName.of("홍길동")).build();

        // when
        String masked = review.getUserName().masked();

        // then
        assertThat(masked).isEqualTo("홍**");
    }
}