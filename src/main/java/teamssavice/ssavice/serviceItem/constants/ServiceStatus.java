package teamssavice.ssavice.serviceItem.constants;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ServiceStatus {
    RECRUITING("모집 중"),
    SUCCEEDED("모집 성공"), // 최소 인원을 충족시켰다는 의미 - 환불이나 취소에서 모집중이랑은 차이가 있음
    CLOSED("모집 마감"), // 이제 더 이상 신청 불가능
    FAILED("모집 실패"),
    CANCELED("모집 취소"),
    COMPLETED("이용 완료"); // 서비스 이용이 종료돼서 리뷰가 가능한 상태

    private final String description;
}