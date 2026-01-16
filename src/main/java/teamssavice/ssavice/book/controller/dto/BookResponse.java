package teamssavice.ssavice.book.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import teamssavice.ssavice.address.AddressResponse;
import teamssavice.ssavice.book.constants.BookStatus;
import teamssavice.ssavice.book.service.dto.BookModel;
import teamssavice.ssavice.serviceItem.constants.ServiceStatus;

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

    // Apply에서 쓰는 유의미한 DisplayStatus 는 RECRUITING 하고 SUCCEEDED 만 있을듯
    @Builder
    public record Apply(
            Long bookId,
            DisplayStatus displayStatus
    ) {
        public static Apply from(BookModel.Apply model) {
            return Apply.builder()
                    .bookId(model.bookId())
                    .displayStatus(toDisplayStatus(model))
                    .build();
        }
    }

    // 여러 상태를 위해서 만들어둠
    private static DisplayStatus toDisplayStatus(BookModel.Apply model) {
        if (model.bookStatus() == BookStatus.CANCELED) {
            return new DisplayStatus(DisplayStatusCode.USER_CANCELED, "유저 취소");
        }

        return switch (model.serviceStatus()) {
            case RECRUITING ->
                    new DisplayStatus(DisplayStatusCode.RECRUITING, "모집 중 (취소 가능)");

            case FAILED ->
                    new DisplayStatus(DisplayStatusCode.FAILED, "모집 실패");

            case CANCELED ->
                    new DisplayStatus(DisplayStatusCode.SERVICE_CANCELED, "업체 취소");
            // 이 부분 관련해서 스케줄러 도입 예정 - ServiceStatus 에서
            case COMPLETED ->
                    new DisplayStatus(DisplayStatusCode.COMPLETED, "이용 완료 (리뷰 작성 가능)");

            case SUCCEEDED, FULLED -> {
                if (model.isInUse()) {
                    yield new DisplayStatus(DisplayStatusCode.IN_USE, "이용 중");
                }

                if (model.isTimeOver()) {
                    yield new DisplayStatus(DisplayStatusCode.COMPLETED, "이용 완료 (리뷰 작성 가능)");
                }

                yield (model.serviceStatus() == ServiceStatus.SUCCEEDED)
                        ? new DisplayStatus(DisplayStatusCode.SUCCEEDED, "모집 성공 (취소 불가능)")
                        : new DisplayStatus(DisplayStatusCode.FULLED, "모집 마감");
            }
        };
    }

    public record DisplayStatus(
            DisplayStatusCode code, // 프론트 로직용
            @Schema(description = "상태 라벨", example = "모집 중")
            String label  // 화면 출력용
    ) {
    }

    public enum DisplayStatusCode {
        RECRUITING, SUCCEEDED, FULLED, IN_USE, FAILED, COMPLETED, SERVICE_CANCELED, USER_CANCELED
    }
}
