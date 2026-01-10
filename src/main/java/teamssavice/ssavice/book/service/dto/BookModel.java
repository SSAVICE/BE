package teamssavice.ssavice.book.service.dto;

import lombok.Builder;
import teamssavice.ssavice.address.AddressModel;
import teamssavice.ssavice.book.constants.BookStatus;
import teamssavice.ssavice.book.entity.Book;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;

import java.time.LocalDateTime;

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

        AddressModel.RegionSummary region,

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
                AddressModel.RegionSummary.builder()
                        .latitude(item.getAddress().getLatitude())
                        .longitude(item.getAddress().getLongitude())
                        .gugun(item.getAddress().getGugun())
                        .region(item.getAddress().getRegion())
                        .build(),

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
