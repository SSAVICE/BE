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
import teamssavice.ssavice.serviceItem.constants.ServiceStatus;
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
    @DisplayName("참여 시 최소 인원이 충족되면 서비스 상태는 '모집 성공'이 되고, 예약은 'RESERVED'로 저장된다")
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

        Book mockBook = BookFixture.book(user, serviceItem, BookStatus.RESERVED);
        given(bookWriteService.save(any(), any(), eq(BookStatus.RESERVED))).willReturn(mockBook);

        // when
        BookModel.Apply result = serviceItemService.apply(userId, serviceId);

        // then

        verify(bookWriteService).save(any(), any(), eq(BookStatus.RESERVED));

        assertThat(serviceItem.getStatus()).isEqualTo(ServiceStatus.SUCCEEDED);

    }

    @Test
    @DisplayName("참여 시 최대 인원이 충족되면 서비스 상태는 '모집 마감(CLOSED)'이 되고, 예약은 'RESERVED'로 저장된다")
    void apply_reaches_maximum_trigger_test() {
        // given
        Long serviceId = 1L;
        Long userId = 2L;

        // 현재 19명, 최대 20명(최소는 이미 넘은 상태)인 서비스 준비
        ServiceItem serviceItem = ServiceItemFixture.custom("축구", LocalDateTime.now().plusDays(1), null, null);
        ReflectionTestUtils.setField(serviceItem, "id", serviceId);
        ReflectionTestUtils.setField(serviceItem, "minimumMember", 10L);
        ReflectionTestUtils.setField(serviceItem, "maximumMember", 20L);
        ReflectionTestUtils.setField(serviceItem, "currentMember", 19L);
        ReflectionTestUtils.setField(serviceItem, "status", ServiceStatus.SUCCEEDED); // 이미 최소인원은 넘은 상태 가정

        Users user = UserFixture.user();

        given(serviceItemReadService.findById(serviceId)).willReturn(serviceItem);
        given(userReadService.findById(userId)).willReturn(user);
        given(bookReadService.existsByUserAndServiceItem(user, serviceItem)).willReturn(false);

        // 저장될 때는 역시나 RESERVED 상태여야 함
        Book mockBook = BookFixture.book(user, serviceItem, BookStatus.RESERVED);
        given(bookWriteService.save(any(), any(), eq(BookStatus.RESERVED))).willReturn(mockBook);

        // when
        BookModel.Apply result = serviceItemService.apply(userId, serviceId);

        // then
        // 1. Book 저장 호출 검증
        verify(bookWriteService).save(any(), any(), eq(BookStatus.RESERVED));

        // 2. 서비스 아이템의 상태가 CLOSED로 변했는지 검증
        assertThat(serviceItem.getStatus()).isEqualTo(ServiceStatus.CLOSED);

        // 3. 인원수가 20명으로 늘어났는지 검증
        assertThat(serviceItem.getCurrentMember()).isEqualTo(20L);
    }



}
