package teamssavice.ssavice.book.constants;

public enum BookStatusFilter {
    // 전체
    ALL,
    RECRUITING, // 모집중: RECRUITING
    SUCCEEDED,  // 모집완료 SUCCEEDED, FULLED, IN_USE
    COMPLETED,  // 이용완료: COMPLETED
    CANCELED    // 취소: FAILED, CANCELED
}
