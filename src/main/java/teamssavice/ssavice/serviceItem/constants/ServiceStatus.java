package teamssavice.ssavice.serviceItem.constants;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ServiceStatus {
    RECRUITING("모집 중"),
    SUCCEEDED("모집 성공"),
    FAILED("모집 실패"),
    CANCELED("모집 취소"),
    COMPLETED("이용 완료");

    private final String description;
}