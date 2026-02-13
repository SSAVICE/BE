package teamssavice.ssavice.review.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import teamssavice.ssavice.auth.constants.Role;
import teamssavice.ssavice.global.annotation.CurrentId;
import teamssavice.ssavice.global.annotation.RequireRole;
import teamssavice.ssavice.global.dto.PageResponse;
import teamssavice.ssavice.review.controller.dto.ReviewRequest;
import teamssavice.ssavice.review.controller.dto.ReviewResponse;
import teamssavice.ssavice.review.service.ReviewService;
import teamssavice.ssavice.review.service.dto.ReviewCommand;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/review")
@Validated
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @RequireRole(Role.USER)
    public ResponseEntity<Void> postReview(
            @CurrentId Long userId,
            @RequestBody @Valid ReviewRequest.Input request
    ) {
        reviewService.saveReview(request.toCommand(userId));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/company/{company-id}")
    public ResponseEntity<PageResponse<ReviewResponse.Item>> getCompanyReview(
            @PathVariable("company-id") @Positive Long companyId,
            @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {
        Page<ReviewResponse.Item> response = reviewService.getReviewByCompanyIdPaging(ReviewCommand.RetrieveByCompanyId.of(companyId, pageable))
                .map(ReviewResponse.Item::from);

        return ResponseEntity.ok(PageResponse.from(response));
    }

    @GetMapping("/company")
    @RequireRole(Role.COMPANY)
    public ResponseEntity<PageResponse<ReviewResponse.Item>> getMyCompanyReview(
            @CurrentId Long companyId,
            @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {
        Page<ReviewResponse.Item> response = reviewService.getReviewByCompanyIdPaging(ReviewCommand.RetrieveByCompanyId.of(companyId, pageable))
                .map(ReviewResponse.Item::from);

        return ResponseEntity.ok(PageResponse.from(response));
    }

    @GetMapping("/user")
    @RequireRole(Role.USER)
    public ResponseEntity<PageResponse<ReviewResponse.Item>> getMyReview(
            @CurrentId Long userId,
            @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {
        Page<ReviewResponse.Item> response = reviewService.getReviewByUserIdPaging(ReviewCommand.RetrieveByUserId.of(userId, pageable))
                .map(ReviewResponse.Item::from);

        return ResponseEntity.ok(PageResponse.from(response));
    }

    @GetMapping("/user/{user-id}")
    @RequireRole(Role.USER)
    public ResponseEntity<PageResponse<ReviewResponse.Item>> getUserReview(
            @PathVariable("user-id") @Positive Long userId,
            @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {
        Page<ReviewResponse.Item> response = reviewService.getReviewByUserIdPaging(ReviewCommand.RetrieveByUserId.of(userId, pageable))
                .map(ReviewResponse.Item::from);

        return ResponseEntity.ok(PageResponse.from(response));
    }
}
