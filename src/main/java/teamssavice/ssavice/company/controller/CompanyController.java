package teamssavice.ssavice.company.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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
    public ResponseEntity<CompanyResponse.Token> login(
            CompanyRequest.Login request
    ) {
        CompanyModel.Login model = companyService.login(request.token());
        return ResponseEntity.ok(CompanyResponse.Token.from(model));
    }
}
