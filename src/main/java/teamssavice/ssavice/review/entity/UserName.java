package teamssavice.ssavice.review.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UserName {
    @NotNull
    @Column(nullable = false)
    private String userName;

    public static UserName of(String userName) {
        return new UserName(userName);
    }

    public String masked() {
        return userName.charAt(0) + "*".repeat(userName.length() - 1);
    }
}
