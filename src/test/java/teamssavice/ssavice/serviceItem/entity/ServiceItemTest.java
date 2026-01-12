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
        ServiceItem item = ServiceItemFixture.custom("테스트", LocalDateTime.now(), null, null);
        ReflectionTestUtils.setField(item, "maximumMember", 10L);
        ReflectionTestUtils.setField(item, "currentMember", 9L);
        ReflectionTestUtils.setField(item, "status", ServiceStatus.RECRUITING);

        // When 한명 참여시
        item.participate();

        // Then 10명이 되면서 서비스 상태가 변하는지
        assertAll(
                () -> assertThat(item.getCurrentMember()).isEqualTo(10L),
                () -> assertThat(item.getStatus()).isEqualTo(ServiceStatus.FINISHED)
        );
    }
}
