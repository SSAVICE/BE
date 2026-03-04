package teamssavice.ssavice.account.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import teamssavice.ssavice.account.controller.dto.AccountRequest;
import teamssavice.ssavice.account.service.AccountService;
import teamssavice.ssavice.auth.constants.Role;
import teamssavice.ssavice.global.annotation.CurrentAuth;
import teamssavice.ssavice.global.annotation.RequireRole;
import teamssavice.ssavice.global.dto.Auth;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account")
public class AccountController {

    private final AccountService accountService;

    @DeleteMapping
    @RequireRole({Role.USER, Role.COMPANY})
    public ResponseEntity<Void> deleteAccount(
        @CurrentAuth Auth auth,
        @RequestBody @Valid AccountRequest.delete request
    ) {
        accountService.delete(auth.id(), request.accessToken());
        return ResponseEntity.noContent().build();
    }
}
