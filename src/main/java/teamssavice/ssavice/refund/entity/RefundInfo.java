package teamssavice.ssavice.refund.entity;

import java.time.LocalDateTime;

public record RefundInfo(
        Long bookId,
        Long userId,
        Long amount,
        String reason,
        LocalDateTime requestedAt
) {
    public static RefundInfo of(Long bookId, Long userId, Long amount, String reason) {
        return new RefundInfo(bookId, userId, amount, reason, LocalDateTime.now());
    }
}
