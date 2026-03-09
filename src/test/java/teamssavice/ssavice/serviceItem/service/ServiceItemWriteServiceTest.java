package teamssavice.ssavice.serviceItem.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;
import teamssavice.ssavice.fixture.ServiceItemFixture;
import teamssavice.ssavice.serviceItem.constants.ServiceStatus;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;
import teamssavice.ssavice.serviceItem.event.ServiceItemAvailabilityChangedEvent;
import teamssavice.ssavice.serviceItem.infrastructure.repository.ServiceItemRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ServiceItemWriteServiceTest {

    @InjectMocks
    private ServiceItemWriteService serviceItemWriteService;

    @Mock
    private ServiceItemRepository serviceItemRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Test
    @DisplayName("참여 시 최소 인원이 충족되면 서비스 상태가 SUCCEEDED로 변경된다")
    void participate_reaches_minimum_status_succeeded() {
        // given
        Long serviceId = 1L;
        ServiceItem serviceItem = ServiceItemFixture.custom("축구", LocalDateTime.now().plusDays(1), null, null);
        ReflectionTestUtils.setField(serviceItem, "id", serviceId);
        ReflectionTestUtils.setField(serviceItem, "minimumMember", 10L);
        ReflectionTestUtils.setField(serviceItem, "maximumMember", 20L);
        ReflectionTestUtils.setField(serviceItem, "currentMember", 9L);

        given(serviceItemRepository.findByIdForUpdate(serviceId)).willReturn(Optional.of(serviceItem));

        // when
        serviceItemWriteService.participate(serviceId);

        // then
        assertThat(serviceItem.getCurrentMember()).isEqualTo(10L);
        assertThat(serviceItem.getStatus()).isEqualTo(ServiceStatus.SUCCEEDED);
        verify(eventPublisher, never()).publishEvent(org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("참여 시 최대 인원이 충족되면 isFull이 되고 이벤트가 발행된다")
    void participate_reaches_maximum_publishes_event() {
        // given
        Long serviceId = 1L;
        ServiceItem serviceItem = ServiceItemFixture.custom("축구", LocalDateTime.now().plusDays(1), null, null);
        ReflectionTestUtils.setField(serviceItem, "id", serviceId);
        ReflectionTestUtils.setField(serviceItem, "minimumMember", 3L);
        ReflectionTestUtils.setField(serviceItem, "maximumMember", 3L);
        ReflectionTestUtils.setField(serviceItem, "currentMember", 2L);

        given(serviceItemRepository.findByIdForUpdate(serviceId)).willReturn(Optional.of(serviceItem));

        // when
        serviceItemWriteService.participate(serviceId);

        // then
        assertThat(serviceItem.getCurrentMember()).isEqualTo(3L);
        assertThat(serviceItem.isFull()).isTrue();

        ArgumentCaptor<ServiceItemAvailabilityChangedEvent> captor =
                ArgumentCaptor.forClass(ServiceItemAvailabilityChangedEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());
        assertThat(captor.getValue().isAvailable()).isFalse();
        assertThat(captor.getValue().serviceItemId()).isEqualTo(serviceId);
    }
}
