package teamssavice.ssavice.serviceItem.constants;

public enum ServiceStatus {
    RECRUITING("모집 중"),
    SUCCESS("모집 성공"),
    FAILED("모집 실패"),
    CANCELED("모집 취소"),
    FINISHED("종료");

    private final String description;

    ServiceStatus(String description) {
        this.description = description;
    }
}