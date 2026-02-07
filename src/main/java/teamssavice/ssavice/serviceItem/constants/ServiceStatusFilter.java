package teamssavice.ssavice.serviceItem.constants;

public enum ServiceStatusFilter {

    ALL,        // 전체
    RECRUITING, // 모집중: RECRUITING
    SUCCEEDED,  // 모집완료 SUCCEEDED, FULLED, IN_USE
    COMPLETED,  // 이용완료: COMPLETED
    CANCELED    // 취소: FAILED, CANCELED
}
