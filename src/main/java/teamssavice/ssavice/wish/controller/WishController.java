package teamssavice.ssavice.wish.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import teamssavice.ssavice.auth.constants.Role;
import teamssavice.ssavice.global.annotation.CurrentId;
import teamssavice.ssavice.global.annotation.RequireRole;
import teamssavice.ssavice.global.dto.PageResponse;
import teamssavice.ssavice.wish.controller.dto.WishRequest;
import teamssavice.ssavice.wish.service.WishService;
import teamssavice.ssavice.wish.service.dto.WishCommand;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/wish")
public class WishController {

    private final WishService wishService;

    @PostMapping("/{serviceId}")
    @RequireRole(Role.USER)
    public ResponseEntity<Void> updateWishStatus(
            @CurrentId Long userId,
            @PathVariable Long serviceId,
            @RequestBody @Valid WishRequest.Update request
    ) {

        wishService.updateWishStatus(request.toCommand(userId, serviceId));
        return ResponseEntity.ok().build();
    }

//    @GetMapping
//    @RequireRole(Role.USER)
//    public ResponseEntity<PageResponse<WishResponse.Summary>> getMyWishList(
//            @CurrentId Long userId,
//            @PageableDefault(page = 0, size = 10) Pageable pageable
//    ) {
//        WishCommand.Retrieve command = WishCommand.Retrieve.of(userId, pageable);
//
//        Page<WishResponse.Summary> responses = wishService.getWishList(command)
//                .map(WishResponse.Summary::from);
//
//        return ResponseEntity.ok(PageResponse.from(responses));
//    }
}
