package teamssavice.ssavice.book.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import teamssavice.ssavice.address.AddressModel;
import teamssavice.ssavice.book.constants.BookStatus;
import teamssavice.ssavice.book.entity.Book;
import teamssavice.ssavice.serviceItem.constants.ServiceStatus;
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

    @Builder
    public record Apply(
            Long bookId,
            DisplayStatus displayStatus
    ) {
        public static Apply from(Book entity) {
            return Apply.builder()
                    .bookId(entity.getId())
                    .displayStatus(toDisplayStatus(entity, entity.getServiceItem()))
                    .build();
        }
    }

    private static DisplayStatus toDisplayStatus(Book book, ServiceItem serviceItem) {

        if (book.getBookStatus() == BookStatus.CANCELED) {
            return new DisplayStatus(DisplayStatusCode.USER_CANCELED, "취소됨");
        }

        return switch (serviceItem.getStatus()) {
            case RECRUITING -> new DisplayStatus(DisplayStatusCode.RECRUITING, "모집 중");
            case SUCCEEDED -> new DisplayStatus(DisplayStatusCode.SUCCEEDED, "예약 확정");
            case CLOSED -> new DisplayStatus(DisplayStatusCode.CLOSED, "모집 마감");
            case FAILED -> new DisplayStatus(DisplayStatusCode.FAILED, "모집 무산");
            case COMPLETED -> new DisplayStatus(DisplayStatusCode.COMPLETED, "이용 완료");
            case CANCELED -> new DisplayStatus(DisplayStatusCode.SERVICE_CANCELED, "업체 취소");
        };
    }

    public record DisplayStatus(
            DisplayStatusCode code, // 프론트 로직용
            @Schema(description = "상태 라벨", example = "모집 중")
            String label  // 화면 출력용
    ) {
    }

    public enum DisplayStatusCode {
        RECRUITING, SUCCEEDED, CLOSED, FAILED, COMPLETED, SERVICE_CANCELED, USER_CANCELED
    }


}
