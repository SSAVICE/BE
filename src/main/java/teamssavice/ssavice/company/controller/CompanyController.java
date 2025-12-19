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
import teamssavice.ssavice.company.service.dto.CompanyCommand;
import teamssavice.ssavice.company.service.dto.CompanyModel;
import teamssavice.ssavice.global.annotation.Authenticate;

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

    @PostMapping("/")
    public ResponseEntity<Void> register(
            @Authenticate Long userId,
            @RequestBody @Valid CompanyRequest.Create request
    ) {
        companyService.register(CompanyCommand.Create.from(userId, request));
        return ResponseEntity.ok().build();
    }
}
