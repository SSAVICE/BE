package teamssavice.ssavice.book.service.dto;

import lombok.Builder;
import teamssavice.ssavice.address.AddressModel;
import teamssavice.ssavice.book.constants.BookViewStatus;
import teamssavice.ssavice.book.entity.BookStatus;
import teamssavice.ssavice.book.entity.Book;
import teamssavice.ssavice.serviceItem.constants.ServiceStatus;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;

import java.time.LocalDateTime;

public class BookModel {

    public record Info(
        Long bookId,
        BookViewStatus bookStatus,
        boolean isReviewed,
        ServiceDetail serviceDetail
    ) {

        public static Info from(Book book) {
            return new Info(
                book.getId(),
                resolve(book.getBookStatus(), book.getServiceItem().getStatus()),
                book.isReviewed(),
                ServiceDetail.from(book.getServiceItem())
            );
        }

        public static BookViewStatus resolve(BookStatus bookstatus, ServiceStatus serviceStatus) {
            if(bookstatus == BookStatus.CANCELED) return BookViewStatus.USER_CANCELED;
            return switch (serviceStatus) {
                case RECRUITING -> BookViewStatus.RECRUITING;
                case SUCCEEDED -> BookViewStatus.SUCCEEDED;
                case FULLED -> BookViewStatus.FULLED;
                case IN_USE -> BookViewStatus.IN_USE;
                case COMPLETED -> BookViewStatus.COMPLETED;
                case FAILED -> BookViewStatus.FAILED;
                case CANCELED -> BookViewStatus.SERVICE_CANCELED;
            };
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

        LocalDateTime startDate,
        LocalDateTime endDate,
        LocalDateTime deadline,
        String tags
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

                item.getStartDate(),
                item.getEndDate(),
                item.getDeadline(),
                item.getTag()
            );
        }
    }

    @Builder
    public record BookSummary(
        Long applying,
        Long completed,
        Long total
    ) {

        public static BookSummary from(
            Long applying,
            Long completed,
            Long total
        ) {
            return BookSummary.builder()
                .applying(applying)
                .completed(completed)
                .total(total)
                .build();
        }
    }

    @Builder
    public record Apply(
            Long bookId
    ) {
        public static Apply of(Long bookId) {
            return Apply.builder()
                    .bookId(bookId)
                    .build();
        }
    }

}
