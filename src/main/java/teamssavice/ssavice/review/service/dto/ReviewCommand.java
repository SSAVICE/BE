package teamssavice.ssavice.review.service.dto;

import lombok.Builder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class ReviewCommand {

    @Builder
    public record Input(
        Long userId,
        Long companyId,
        Long serviceId,
        Integer rating,
        String comment
    ) {
    }

    @Builder
    public record RetrieveByCompanyId(
        Long companyId,
        Pageable pageable
    ) {
        public static RetrieveByCompanyId of(Long companyId, Pageable pageable) {
            return RetrieveByCompanyId.builder()
                .companyId(companyId)
                .pageable(PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by("createdAt").descending())
                )
                .build();
        }
    }
}
