package teamssavice.ssavice.serviceItem.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import teamssavice.ssavice.fixture.ServiceItemFixture;
import teamssavice.ssavice.serviceItem.constants.ServiceStatus;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;
import teamssavice.ssavice.serviceItem.infrastructure.repository.ServiceItemRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ServiceItemWriteServiceTest {

    @InjectMocks
    private ServiceItemWriteService serviceItemWriteService;

    @Mock
    private ServiceItemRepository serviceItemRepository;

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
    }

    @Test
    @DisplayName("참여 시 최대 인원이 충족되면 isFull이 된다")
    void participate_reaches_maximum_is_full() {
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
    }
}
