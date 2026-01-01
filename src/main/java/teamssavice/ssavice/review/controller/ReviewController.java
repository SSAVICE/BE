package teamssavice.ssavice.review.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import teamssavice.ssavice.auth.constants.Role;
import teamssavice.ssavice.global.annotation.CurrentId;
import teamssavice.ssavice.global.annotation.RequireRole;
import teamssavice.ssavice.global.dto.PagingResponse;
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

    @RequireRole(Role.USER)
    @PostMapping
    public ResponseEntity<Void> postReview(
            @CurrentId Long userId,
            @RequestBody @Valid ReviewRequest.Input request
    ) {
        reviewService.saveReview(ReviewCommand.Input.from(userId, request));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{company-id}")
    public PagingResponse<ReviewResponse.Item> getReview(
            @PathVariable("company-id") @Positive Long companyId,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<ReviewResponse.Item> response = reviewService.getReviewPaging(companyId, pageable)
                .map(ReviewResponse.Item::from);
        return PagingResponse.from(response);
    }
}
