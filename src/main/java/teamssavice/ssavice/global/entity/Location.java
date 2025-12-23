package teamssavice.ssavice.global.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
/**
 *  공통 위치 정보 객체
 *  DB 스키마 상에서 모든 필드 NULL 허용
 */
public class Location {

    private Double longitude;       // 경도
    private Double latitude;        // 위도
    private String postCode;        // 우편번호
    private String address;         // 주소
    private String detailAddress;

    private String region1;         // 시/도
    private String region2;         // 구/군
    private String region1Code;     // 시/도 코드
    private String region2Code;
}
