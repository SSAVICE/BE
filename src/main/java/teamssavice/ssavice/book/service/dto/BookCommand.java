package teamssavice.ssavice.book.service.dto;


import org.springframework.data.domain.Pageable;

public class BookCommand {

    public record Retrieve(
            Long userId,
            Pageable pageable
    ) {
        public static Retrieve of(Long userId, Pageable pageable) {
            return new Retrieve(userId, pageable);
        }
    }
}
