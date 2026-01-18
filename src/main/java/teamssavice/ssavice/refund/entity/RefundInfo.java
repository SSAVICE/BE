package teamssavice.ssavice.refund.entity;

import teamssavice.ssavice.refund.constants.RefundReason;

import java.time.LocalDateTime;

public record RefundInfo(
        Long bookId,
        Long userId,
        Long amount,
        RefundReason reason,
        LocalDateTime requestedAt
) {
    public static RefundInfo of(Long bookId, Long userId, Long amount, RefundReason reason) {
        return new RefundInfo(bookId, userId, amount, reason, LocalDateTime.now());
    }
}
