package teamssavice.ssavice.wish.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import teamssavice.ssavice.auth.constants.Role;
import teamssavice.ssavice.global.annotation.CurrentAuth;
import teamssavice.ssavice.global.annotation.RequireRole;
import teamssavice.ssavice.global.dto.Auth;
import teamssavice.ssavice.global.dto.PageResponse;
import teamssavice.ssavice.wish.controller.dto.WishRequest;
import teamssavice.ssavice.wish.controller.dto.WishResponse;
import teamssavice.ssavice.wish.service.WishService;
import teamssavice.ssavice.wish.service.dto.WishCommand;
import teamssavice.ssavice.wish.service.dto.WishModel;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/wish")
public class WishController {

    private final WishService wishService;

    @PostMapping("/{serviceId}")
    @RequireRole(Role.USER)
    public ResponseEntity<Void> updateWishStatus(
            @CurrentAuth Auth authUser,
            @PathVariable Long serviceId,
            @RequestBody @Valid WishRequest.Update request
    ) {

        wishService.updateWishStatus(request.toCommand(authUser.id(), serviceId));
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @RequireRole(Role.USER)
    public ResponseEntity<PageResponse<WishResponse.Summary>> getMyWishList(
            @CurrentAuth Auth authUser,
            @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {

        WishCommand.Retrieve command = WishCommand.Retrieve.of(authUser.id(), pageable);

        Page<WishModel.Summary> modelPage = wishService.getWishList(command);
        Page<WishResponse.Summary> responses = modelPage.map(WishResponse.Summary::from);

        return ResponseEntity.ok(PageResponse.from(responses));
    }
}
