package teamssavice.ssavice.global.dto;

import teamssavice.ssavice.auth.constants.Role;

public record Auth(
    Long id,
    Role role
) {
    public Long getIdIfCanAccess(Role required) {
        return canAccess(required) ? id : null;
    }

    public boolean canAccess(Role required) {
        return required.canAccess(role);
    }
}
