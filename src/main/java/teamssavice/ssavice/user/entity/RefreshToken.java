package teamssavice.ssavice.user.entity;

import lombok.Builder;
import lombok.Getter;

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

    public boolean isExpired() {
        Date now = new Date();
        Date accessValidity = new Date(issuedAt.getTime() + expiresIn);
        return now.after(accessValidity);
    }
}
