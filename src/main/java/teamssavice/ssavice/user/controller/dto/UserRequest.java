package teamssavice.ssavice.user.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import teamssavice.ssavice.user.service.dto.UserCommand;

public class UserRequest {

    @Builder
    public record Login(
        @NotNull
        String token
    ) {

    }

    @Builder
    public record Modify(
        @NotBlank
        String name,
        @NotBlank
        String email,
        @NotBlank
        String phoneNumber
    ) {
        public UserCommand.Modify toCommand(Long userId) {
            return UserCommand.Modify.builder()
                    .userId(userId)
                    .name(name)
                    .email(email)
                    .phoneNumber(phoneNumber)
                    .build();
        }
    }
}
