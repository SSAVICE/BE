package teamssavice.ssavice.wish.controller.dto;

import jakarta.validation.constraints.NotNull;
import teamssavice.ssavice.wish.service.dto.WishCommand;

public class WishRequest {
    public record Update(
            @NotNull
            Boolean targetStatus
    ) {
        public WishCommand.UpdateStatus toCommand(Long userId, Long serviceId) {
            return WishCommand.UpdateStatus.of(userId, serviceId, targetStatus);
        }
    }
}