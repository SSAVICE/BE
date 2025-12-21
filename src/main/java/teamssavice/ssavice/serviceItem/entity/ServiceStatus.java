package teamssavice.ssavice.serviceItem.entity;

public enum ServiceStatus {
    BEFORE_RECRUITING("모집 예정"),
    RECRUITING("모집 중"),
    FINISHED("종료");

    private final String description;

    ServiceStatus(String description) {
        this.description = description;
    }
}