package teamssavice.ssavice.user.service.dto;

import lombok.Builder;
import teamssavice.ssavice.user.controller.dto.UserRequest;

public class UserCommand {

    @Builder
    public record Modify(
        Long userId,
        String name,
        String email,
        String phoneNumber
    ) {

        public static UserCommand.Modify from(Long userId, UserRequest.Modify request) {
            return Modify.builder()
                .userId(userId)
                .name(request.name())
                .email(request.email())
                .phoneNumber(request.phoneNumber())
                .build();
        }
    }

}
