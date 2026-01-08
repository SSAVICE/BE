package teamssavice.ssavice.address;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Region {

    @Id
    @Column(length = 10, columnDefinition = "CHAR(10)")
    private String regionCode;

    // gugunCode에 매핑되는 문자열
    @NotNull
    @Column(nullable = false)
    private String gugun;

    // regionCode 에 매핑되는 문자열
    @Column(nullable = false)
    private String region;


}
