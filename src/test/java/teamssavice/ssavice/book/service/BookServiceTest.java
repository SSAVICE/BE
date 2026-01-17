package teamssavice.ssavice.book.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import teamssavice.ssavice.book.entity.BookStatus;
import teamssavice.ssavice.book.entity.Book;
import teamssavice.ssavice.book.service.dto.BookModel;
import teamssavice.ssavice.fixture.BookFixture;
import teamssavice.ssavice.fixture.ServiceItemFixture;
import teamssavice.ssavice.fixture.UserFixture;
import teamssavice.ssavice.user.entity.Users;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @InjectMocks
    private BookService bookService;

    @Mock
    private BookReadService bookReadService;

    private final List<Book> books = new ArrayList<>();

    @BeforeEach
    void setUp() {
        Users user = UserFixture.user();
        for (int i = 0; i < 5; i++) {
            books.add(BookFixture.book(user, ServiceItemFixture.fulled(null), BookStatus.RESERVED));
            books.add(BookFixture.book(user, ServiceItemFixture.recruiting(null), BookStatus.RESERVED));
        }
    }

    @Test
    @DisplayName("사용자의 예약 요약 정보 조회 시, 각 상태별 카운트가 정확히 합산되어 반환된다")
    void getBookSummary_test() {
        // given
        Long userId = 1L;

        // Mock 데이터 설정
        Long recruitingCount = 5L; // 모집 중
        Long completedCount = 5L;  // 모집 성공 및 마감

        // 각 상태별로 호출될 때 반환할 값 지정
        given(bookReadService.findByUserIdAndBookStatus(userId, BookStatus.RESERVED))
                .willReturn(books);

        // when
        BookModel.BookSummary result = bookService.getBookSummary(userId);

        // then
        // 1. 결과 DTO의 필드 검증
        // applying = recruitingCount (5)
        assertThat(result.applying()).isEqualTo(5L);

        // completed(모집완료) = succeededCount(3) + closedCount(2) = 5
        assertThat(result.completed()).isEqualTo(5L);

        // 2. 각 메서드가 정확히 호출되었는지 검증
        verify(bookReadService).findByUserIdAndBookStatus(
                userId, BookStatus.RESERVED);
    }
}