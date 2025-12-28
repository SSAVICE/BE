package teamssavice.ssavice.book.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BookStatus {

    APPLYING("모집 신청 중"),  // 아직 인원이 안 모였거나 마감 전
    MATCHED("모집 성공"),     // 확정: 최소 인원이 모여서 공구 성사됨
    FAILED("모집 무산"),      // 실패: 기간 내 인원 미달로 취소됨
    CANCELED("신청 취소"),    // 취소: 사용자가 직접 취소함
    COMPLETED("이용 완료");   // 완료: 서비스 이용이 끝남 (리뷰 작성 가능 상태)

    private final String description;
}