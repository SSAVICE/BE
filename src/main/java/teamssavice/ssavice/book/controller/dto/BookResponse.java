package teamssavice.ssavice.book.controller.dto;

import lombok.Builder;
import teamssavice.ssavice.address.AddressResponse;
import teamssavice.ssavice.book.service.dto.BookModel;

import java.time.LocalDateTime;

public class BookResponse {

    public record Info(
        ServiceInfo serviceInfo,
        String bookStatus,
        boolean isReviewed
    ) {

        public static Info from(BookModel.Info model) {
            return new Info(
                ServiceInfo.from(model.serviceDetail()),
                model.bookStatus().name(),
                model.isReviewed()
            );
        }
    }

    public record ServiceInfo(
        Long serviceId,
        String thumbnailUrl,
        String category,
        String companyName,
        Long companyId,
        String title,
        // 위치 정보
        AddressResponse.RegionSummary region,
        // 인원
        Long currentMember,
        Long minimumMember,
        Long maximumMember,
        // 가격
        Long basePrice,
        Integer discountRate,
        Long discountedPrice,
        // 기타
        LocalDateTime deadline,
        String tag,
        String status
    ) {

        public static ServiceInfo from(BookModel.ServiceDetail model) {
            return new ServiceInfo(
                model.serviceId(),
                model.imageUrl(), // Model의 필드명(thumbnailUrl 등)을 가져옴
                model.category(),
                model.companyName(),
                model.companyId(),
                model.title(),

                AddressResponse.RegionSummary.from(model.region()),

                model.currentMember(),
                model.minMember(),
                model.maxMember(),

                model.basePrice(),
                model.discountRate(),
                model.discountedPrice(),

                model.deadline(),
                model.tags(), // List<String> 그대로 전달
                model.status()
            );
        }
    }

    @Builder
    public record BookSummary(
        Long total,
        Long applying,
        Long completed
    ) {

        public static BookSummary from(BookModel.BookSummary model) {
            return BookSummary.builder()
                .total(model.total())
                .applying(model.applying())
                .completed(model.completed())
                .build();
        }
    }
}
