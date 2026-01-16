package teamssavice.ssavice.book.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BookStatus {

    RESERVED("예약 완료"),  // 유저가 신청 버튼을 누른 상태
    CANCELED("예약 취소"); // 신청취소를 누르면

    private final String description;
}