package teamssavice.ssavice.auth;

import lombok.Builder;
import lombok.Getter;
import teamssavice.ssavice.auth.constants.Role;

import java.util.Date;

@Builder
@Getter
public class RefreshToken {
    private Long userId;
    private Date issuedAt;
    private Long expiresIn;
    private Role role;
    private boolean revoked;

    public void revoke() {
        this.revoked = true;
    }

    public boolean isExpired() {
        Date now = new Date();
        Date accessValidity = new Date(issuedAt.getTime() + expiresIn);
        return now.after(accessValidity);
    }
}
