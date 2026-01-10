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

    public void update(AddressCommand.Update command) {
        if (command.latitude() != null) {
            this.latitude = command.latitude();
        }
        if (command.longitude() != null) {
            this.longitude = command.longitude();
        }
        if (command.postCode() != null) {
            this.postCode = command.postCode();
        }
        if (command.address() != null) {
            this.address = command.address();
        }
        if (command.detailAddress() != null) {
            this.detailAddress = command.detailAddress();
        }
    }
}
