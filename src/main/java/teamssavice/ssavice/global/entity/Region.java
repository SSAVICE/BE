package teamssavice.ssavice.global.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(indexes = {
        @Index(name = "idx_region_filter", columnList = "gugun_code, depth")
})
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Region {

    @Id
    @Column(length = 10, columnDefinition = "CHAR(10)")
    private String regionCode;

    @NotNull
    @Column(nullable = false)
    private String fullAddress;


}
