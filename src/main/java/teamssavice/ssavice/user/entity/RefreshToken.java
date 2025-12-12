package teamssavice.ssavice.user.entity;

import lombok.Builder;
import lombok.Getter;
import teamssavice.ssavice.user.constants.Role;

import java.util.Date;

@Builder
@Getter
public class RefreshToken {
    private Long userId;
    private Date issuedAt;
    private Long expiresIn;
    private boolean revoked;

    public void refresh() {
        this.revoked = true;
    }
}
