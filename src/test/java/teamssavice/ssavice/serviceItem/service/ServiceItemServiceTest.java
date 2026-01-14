package teamssavice.ssavice.serviceItem.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import teamssavice.ssavice.book.constants.BookStatus;
import teamssavice.ssavice.book.entity.Book;
import teamssavice.ssavice.book.service.BookReadService;
import teamssavice.ssavice.book.service.BookWriteService;
import teamssavice.ssavice.book.service.dto.BookModel;
import teamssavice.ssavice.fixture.BookFixture;
import teamssavice.ssavice.fixture.ServiceItemFixture;
import teamssavice.ssavice.fixture.UserFixture;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;
import teamssavice.ssavice.user.entity.Users;
import teamssavice.ssavice.user.service.UserReadService;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ServiceItemServiceTest {

    @InjectMocks
    private ServiceItemService serviceItemService;

    @Mock private ServiceItemReadService serviceItemReadService;
    @Mock private UserReadService userReadService;
    @Mock private BookReadService bookReadService;
    @Mock private BookWriteService bookWriteService;

    @Test
    @DisplayName("내가 참여해서 딱 최소 인원이 되면, 전체 상태 변경 메서드가 호출되고 내 상태는 MATCHED가 된다")
    void apply_reaches_minimum_trigger_test() {
        // given
        Long serviceId = 1L;
        Long userId = 2L;

        ServiceItem serviceItem = ServiceItemFixture.custom("축구", LocalDateTime.now().plusDays(1), null, null);
        ReflectionTestUtils.setField(serviceItem, "id", serviceId);
        ReflectionTestUtils.setField(serviceItem, "minimumMember", 10L);
        ReflectionTestUtils.setField(serviceItem, "currentMember", 9L);

        Users user = UserFixture.user();

        given(serviceItemReadService.findById(serviceId)).willReturn(serviceItem);
        given(userReadService.findById(userId)).willReturn(user);
        given(bookReadService.existsByUserAndServiceItem(user, serviceItem)).willReturn(false);

        Book mockBook = BookFixture.book(user, serviceItem, BookStatus.MATCHED);
        given(bookWriteService.save(any(), any(), any())).willReturn(mockBook);

        // when
        BookModel.Apply result = serviceItemService.apply(userId, serviceId);

        // then
        verify(bookWriteService, times(1)).updateAllStatusToMatched(serviceId);

        verify(bookWriteService).save(any(), any(), eq(BookStatus.MATCHED));

        assertThat(result.bookStatus()).isEqualTo(BookStatus.MATCHED);

    }



}
