package teamssavice.ssavice.company.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import teamssavice.ssavice.company.controller.dto.CompanyRequest;
import teamssavice.ssavice.company.controller.dto.CompanyResponse;
import teamssavice.ssavice.company.service.CompanyService;
import teamssavice.ssavice.company.service.dto.CompanyModel;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/company")
public class CompanyController {
    private final CompanyService companyService;

    @PostMapping("/login")
    public ResponseEntity<CompanyResponse.Login> login(
            @RequestBody @Valid CompanyRequest.Login request
    ) {
        CompanyModel.Login model = companyService.login(request.token());
        return ResponseEntity.ok(CompanyResponse.Login.from(model));
    }

    @PostMapping("/refresh")
    public ResponseEntity<CompanyResponse.Token> refresh(
            @RequestBody @Valid CompanyRequest.Refresh request
    ) {
        CompanyModel.Refresh model = companyService.refresh(request.refreshToken());
        return ResponseEntity.ok(CompanyResponse.Token.from(model));
    }
}
