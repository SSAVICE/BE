package teamssavice.ssavice.company.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import teamssavice.ssavice.auth.constants.Role;
import teamssavice.ssavice.company.controller.dto.CompanyRequest;
import teamssavice.ssavice.company.controller.dto.CompanyResponse;
import teamssavice.ssavice.company.service.CompanyService;
import teamssavice.ssavice.company.service.dto.CompanyCommand;
import teamssavice.ssavice.company.service.dto.CompanyModel;
import teamssavice.ssavice.global.annotation.CurrentId;
import teamssavice.ssavice.global.annotation.RequireRole;
import teamssavice.ssavice.imageresource.ImageRequest;
import teamssavice.ssavice.imageresource.ImageResponse;
import teamssavice.ssavice.imageresource.constants.ImageContentType;
import teamssavice.ssavice.imageresource.constants.ImagePath;
import teamssavice.ssavice.imageresource.service.ImageService;
import teamssavice.ssavice.imageresource.service.dto.ImageModel;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/company")
@Validated
public class CompanyController {
    private final CompanyService companyService;
    private final ImageService imageService;

    @PostMapping("/login")
    public ResponseEntity<CompanyResponse.Login> login(
            @RequestBody @Valid CompanyRequest.Login request
    ) {
        CompanyModel.Login model = companyService.login(request.token());
        return ResponseEntity.ok(CompanyResponse.Login.from(model));
    }

    @RequireRole(Role.USER)
    @PostMapping
    public ResponseEntity<CompanyResponse.Login> register(
            @CurrentId Long userId,
            @RequestBody @Valid CompanyRequest.Create request
    ) {
        CompanyModel.Login model = companyService.register(CompanyCommand.Create.from(userId, request));
        return ResponseEntity.ok(CompanyResponse.Login.from(model));
    }

    @RequireRole(Role.COMPANY)
    @PutMapping
    public ResponseEntity<CompanyResponse> putCompany(
            @CurrentId Long companyId,
            @RequestBody @Valid CompanyRequest.Update request
    ) {
        companyService.updateCompany(CompanyCommand.Update.from(companyId, request));
        return ResponseEntity.ok().build();
    }

    @RequireRole(Role.COMPANY)
    @GetMapping
    public ResponseEntity<CompanyResponse.MyCompany> getCompany(
            @CurrentId Long companyId
    ) {
        CompanyModel.MyCompany model = companyService.getMyCompany(companyId);
        return ResponseEntity.ok(CompanyResponse.MyCompany.from(model));
    }

    @GetMapping("/{company-id}")
    public ResponseEntity<CompanyResponse.Info> getCompanyById(
            @PathVariable("company-id") @Positive Long companyId
    ) {
        CompanyModel.Info model = companyService.getCompanyById(companyId);
        return ResponseEntity.ok(CompanyResponse.Info.from(model));
    }

    @GetMapping("/{company-id}/summary")
    public ResponseEntity<CompanyResponse.Summary> getCompanySummary(
            @PathVariable("company-id") @Positive Long companyId
    ) {
        CompanyModel.Summary model = companyService.getCompanySummary(companyId);
        return ResponseEntity.ok(CompanyResponse.Summary.from(model));
    }

    @PostMapping("/image")
    @RequireRole(Role.COMPANY)
    public ResponseEntity<ImageResponse.Presigned> createCompanyPresignedUrl(
            @CurrentId Long userId,
            @RequestBody @Valid ImageRequest.ContentType request
    ) {
        ImageModel.PutPresigned model = imageService.updateProfileImage(userId, ImagePath.company, ImageContentType.from(request.contentType()));
        return ResponseEntity.ok(ImageResponse.Presigned.from(model));
    }

    @PostMapping("/image/confirm")
    @RequireRole(Role.COMPANY)
    public ResponseEntity<Void> confirmCompanyImageUpload(
            @CurrentId Long companyId,
            @RequestBody @Valid ImageRequest.Confirm request
    ) {
        companyService.updateCompanyImage(companyId, request.objectKey());
        return ResponseEntity.ok().build();
    }
}
