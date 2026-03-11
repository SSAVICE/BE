package teamssavice.ssavice.serviceItem.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import teamssavice.ssavice.fixture.ServiceItemFixture;
import teamssavice.ssavice.serviceItem.constants.ServiceStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class ServiceItemTest {

    @Test
    @DisplayName("최대 인원에 도달하도록 참여하면 서비스 상태가 FINISHED로 자동 변경된다")
    void entity_logic_test() {
        // Given: 최대 인원 10명 현재 9명
        ServiceItem item = ServiceItemFixture.custom("테스트", LocalDateTime.now().plusDays(5), null, null);
        ReflectionTestUtils.setField(item, "minimumMember", 10L);
        ReflectionTestUtils.setField(item, "maximumMember", 11L);
        ReflectionTestUtils.setField(item, "currentMember", 9L);

        // When 한명 참여시
        item.participate();
        // Then 10명이 되면서 서비스 상태가 변하는지
        assertAll(
                () -> assertThat(item.getCurrentMember()).isEqualTo(10L),
                () -> assertThat(item.getStatus()).isEqualTo(ServiceStatus.SUCCEEDED)
        );
    }

    @Test
    @DisplayName("최대 인원에 도달하면 isFull이 true가 된다")
    void participate_reaches_maximum_becomes_full() {
        // Given: 최소 3명, 최대 3명, 현재 2명
        ServiceItem item = ServiceItemFixture.custom("테스트", LocalDateTime.now().plusDays(5), null, null);
        ReflectionTestUtils.setField(item, "minimumMember", 3L);
        ReflectionTestUtils.setField(item, "maximumMember", 3L);
        ReflectionTestUtils.setField(item, "currentMember", 2L);

        // When
        item.participate();

        // Then
        assertAll(
                () -> assertThat(item.getCurrentMember()).isEqualTo(3L),
                () -> assertThat(item.isFull()).isTrue(),
                () -> assertThat(item.getStatus()).isEqualTo(ServiceStatus.FULLED)
        );
    }
}
