package teamssavice.ssavice.imageresource.constants;

public enum ImageStatus {
    PENDING,   // 아직 S3 copy 안 됨
    DONE,      // copy 성공 → 사용 가능
    FAILED,    // copy 실패 → 재시도/정리 대상
    PROCESSING // 처리 중(선점됨)
}

