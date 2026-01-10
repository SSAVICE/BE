package teamssavice.ssavice.address;

import jakarta.persistence.*;
import lombok.*;
import teamssavice.ssavice.global.entity.BaseEntity;

import java.math.BigDecimal;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder
@Getter
@AllArgsConstructor
public class Address extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String gugun;
    @Column
    private String gugunCode;
    @Column
    private String region;
    @Column
    private String regionCode;
    @Column(precision = 10, scale = 7)
    private BigDecimal latitude;
    @Column(precision = 10, scale = 7)
    private BigDecimal longitude;
    @Column
    private String postCode;
    @Column
    private String address;
    @Column
    private String detailAddress;

    public void update(AddressCommand.RegionInfo command) {
        this.gugun = command.gugun();
        this.gugunCode = command.gugunCode();
        this.region = command.region();
        this.regionCode = command.regionCode();
        this.latitude = command.latitude();
        this.longitude = command.longitude();
        this.postCode = command.postCode();
        this.address = command.address();
        this.detailAddress = command.detailAddress();
    }
}
