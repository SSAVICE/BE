package teamssavice.ssavice.book.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import teamssavice.ssavice.book.service.dto.BookModel;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @InjectMocks
    private BookService bookService;

    @Mock
    private BookReadService bookReadService;

    @Test
    @DisplayName("사용자의 예약 요약 정보 조회 시, 각 상태별 카운트가 정확히 합산되어 반환된다")
    void getBookSummary_test() {
        // given
        Long userId = 1L;

        // Mock 데이터 설정
        Long recruitingCount = 5L; // 모집 중
        Long completedCount = 6L;  // 모집 성공 및 마감

        // 각 상태별로 호출될 때 반환할 값 지정
        given(bookReadService.countRecruitingBooksByUserId(userId))
                .willReturn(recruitingCount);
        given(bookReadService.countSucceededBooksByUserId(userId))
                .willReturn(completedCount);

        // when
        BookModel.BookSummary result = bookService.getBookSummary(userId);

        // then
        // 1. 결과 DTO의 필드 검증
        assertThat(result.applying()).isEqualTo(recruitingCount);

        assertThat(result.completed()).isEqualTo(completedCount);
    }
}