package teamssavice.ssavice.book.service.dto;


import lombok.Builder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
                .pageable(PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by("createdAt").descending()
                ))
                .status(status)
                .build();
        }
    }

}
