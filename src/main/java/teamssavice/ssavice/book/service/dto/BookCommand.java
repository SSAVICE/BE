package teamssavice.ssavice.book.service.dto;


import lombok.Builder;
import org.springframework.data.domain.Pageable;
import teamssavice.ssavice.book.constants.BookStatus;

public class BookCommand {

    public record Retrieve(
        Long userId,
        Pageable pageable
    ) {

        public static Retrieve of(Long userId, Pageable pageable) {
            return new Retrieve(userId, pageable);
        }
    }

    @Builder
    public record RetrieveByStatus(
        Long userId,
        Pageable pageable,
        BookStatus status
    ) {

        public static RetrieveByStatus of(Long userId, Pageable pageable, BookStatus status) {
            return RetrieveByStatus.builder()
                .userId(userId)
                .pageable(pageable)
                .status(status)
                .build();
        }
    }
}
