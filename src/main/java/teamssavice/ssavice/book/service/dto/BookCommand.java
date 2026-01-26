package teamssavice.ssavice.book.service.dto;


import lombok.Builder;
import org.springframework.data.domain.Pageable;
import teamssavice.ssavice.book.constants.BookStatusFilter;

public class BookCommand {

    @Builder
    public record RetrieveByStatus(
        Long id,
        Pageable pageable,
        BookStatusFilter status
    ) {

        public static RetrieveByStatus of(Long id, Pageable pageable, BookStatusFilter status) {
            return RetrieveByStatus.builder()
                .id(id)
                .pageable(pageable)
                .status(status)
                .build();
        }
    }

}
