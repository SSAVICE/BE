package teamssavice.ssavice.serviceItem.constants;

public enum ServiceStatus {
    RECRUITING("모집 중"),
    FINISHED("종료");

    private final String description;

    ServiceStatus(String description) {
        this.description = description;
    }
}