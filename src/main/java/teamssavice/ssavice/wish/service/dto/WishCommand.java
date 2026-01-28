package teamssavice.ssavice.wish.service.dto;

import lombok.Builder;
import org.springframework.data.domain.Pageable;

public class WishCommand {

    @Builder
    public record UpdateStatus(
            Long userId,
            Long serviceId,
            boolean targetStatus
    ) {
        public static UpdateStatus of(Long userId, Long serviceId, boolean targetStatus) {
            return UpdateStatus.builder()
                    .userId(userId)
                    .serviceId(serviceId)
                    .targetStatus(targetStatus)
                    .build();
        }
    }

    @Builder
    public record Retrieve(
            Long userId,
            Pageable pageable
    ) {
        public static Retrieve of(Long userId, Pageable pageable) {
            return Retrieve.builder()
                    .userId(userId)
                    .pageable(pageable)
                    .build();
        }
    }
}
