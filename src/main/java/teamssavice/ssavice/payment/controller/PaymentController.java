package teamssavice.ssavice.payment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import teamssavice.ssavice.auth.constants.Role;
import teamssavice.ssavice.global.annotation.CurrentAuth;
import teamssavice.ssavice.global.annotation.RequireRole;
import teamssavice.ssavice.global.dto.Auth;
import teamssavice.ssavice.payment.controller.dto.PaymentRequest;
import teamssavice.ssavice.payment.controller.dto.PaymentResponse;
import teamssavice.ssavice.payment.service.PaymentService;
import teamssavice.ssavice.payment.service.dto.PaymentModel;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/prepare")
    @RequireRole(Role.USER)
    public ResponseEntity<PaymentResponse.Prepare> prepare(
        @CurrentAuth Auth authUser,
        @RequestBody @Valid PaymentRequest.Prepare request
    ) {
        PaymentModel.Prepare model = paymentService.createPendingPayment(request.toCommand(authUser.id()));
        return ResponseEntity.ok(PaymentResponse.Prepare.from(model));
    }

    @PostMapping("/confirm")
    @RequireRole(Role.USER)
    public ResponseEntity<Void> confirm(
        @CurrentAuth Auth authUser,
        @RequestBody @Valid PaymentRequest.Confirm request
    ) {
        paymentService.confirmPayment(request.toCommand(authUser.id()));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/fail")
    @RequireRole(Role.USER)
    public ResponseEntity<Void> fail(
        @CurrentAuth Auth authUser,
        @RequestBody @Valid PaymentRequest.Fail request
    ) {
        paymentService.failPayment(request.toCommand(authUser.id()));
        return ResponseEntity.ok().build();
    }
}
