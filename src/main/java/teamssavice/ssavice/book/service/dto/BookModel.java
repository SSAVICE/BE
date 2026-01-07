package teamssavice.ssavice.book.service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import teamssavice.ssavice.book.constants.BookStatus;
import teamssavice.ssavice.book.entity.Book;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;

public class BookModel {

    public record Info(
        Long bookId,
        BookStatus bookStatus,
        boolean isReviewed,
        ServiceDetail serviceDetail
    ) {

        public static Info from(Book book) {
            return new Info(
                book.getId(),
                book.getBookStatus(),
                book.isReviewed(),
                ServiceDetail.from(book.getServiceItem())
            );
        }
    }

    public record ServiceDetail(
        Long serviceId,
        String title,
        String imageUrl,
        String category,
        String companyName,
        Long companyId,

        String region1,
        String region2,
        BigDecimal latitude,
        BigDecimal longitude,

        Long currentMember,
        Long minMember,
        Long maxMember,
        Long basePrice,
        Integer discountRate,
        Long discountedPrice,

        LocalDateTime deadline,
        String tags,
        String status
    ) {

        public static ServiceDetail from(ServiceItem item) {

            return new ServiceDetail(
                item.getId(),
                item.getTitle(),
                item.getThumbnailUrl(),
                item.getCategory(),

                item.getCompany().getCompanyName(),
                item.getCompany().getId(),
                item.getAddress().getRegion1(),
                item.getAddress().getRegion2(),
                item.getAddress().getLatitude(),
                item.getAddress().getLongitude(),

                item.getCurrentMember(),
                item.getMinimumMember(),
                item.getMaximumMember(),

                item.getPrice().getBasePrice(),
                item.getPrice().getDiscountRate(),
                item.getPrice().getDiscountedPrice(),

                item.getDeadline(),
                item.getTag(),
                item.getStatus().name()
            );
        }
    }

    @Builder
    public record BookSummary(
        Long applying,
        Long completed
    ) {

        public static BookSummary from(
            Long applying,
            Long completed
        ) {
            return BookSummary.builder()
                .applying(applying)
                .completed(completed)
                .build();
        }

        public Long total() {
            return applying + completed;
        }
    }
}
