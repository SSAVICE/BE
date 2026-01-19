package teamssavice.ssavice.book.constants;

public enum BookStatusFilter {
    // 전체
    ALL,
    RECRUITING, // 모집중: RECRUITING, SUCCEEDED
    COMPLETED,  // 모집완료: FULLED, IN_USE, COMPLETED
    CANCELED    // 취소: FAILED, CANCELED
}
