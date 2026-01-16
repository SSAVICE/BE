package teamssavice.ssavice.serviceItem.constants;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ServiceStatus {
    RECRUITING("모집 중"), // 최소 인원 미충족 + 모집 마감 전
    SUCCEEDED("모집 성공"), // 최소 인원 충족 + 모집 마감 전
    FULLED("모집 마감"), // (최대 인원 충족) OR (최소 인원 충족 + 모집마감)
    FAILED("모집 실패"), // 최소 인원 미충족 + 모집 마감
    CANCELED("모집 취소"), // 게시자가 삭제
    INUSE("이용 중"), // 모집 마감된 서비스가 서비스되는 기간 중에 있음
    COMPLETED("이용 완료"); // 모집 마감된 서비스가 서비스 이용이 종료

    private final String description;
}