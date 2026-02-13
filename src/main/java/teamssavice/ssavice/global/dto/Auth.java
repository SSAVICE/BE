package teamssavice.ssavice.global.dto;

import teamssavice.ssavice.auth.constants.Role;

public record Auth(
    Long id,
    Role role
) {
}
