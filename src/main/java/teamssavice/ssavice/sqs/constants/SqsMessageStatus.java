package teamssavice.ssavice.sqs.constants;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SqsMessageStatus {
    DONE,      // 처리 성공
    FAILED     // 처리 실패
}
