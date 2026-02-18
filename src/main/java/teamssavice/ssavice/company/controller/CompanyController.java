package teamssavice.ssavice.company.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import teamssavice.ssavice.address.AddressModel;
import teamssavice.ssavice.address.AddressResponse;
import teamssavice.ssavice.auth.constants.Role;
import teamssavice.ssavice.company.controller.dto.CompanyRequest;
import teamssavice.ssavice.company.controller.dto.CompanyResponse;
import teamssavice.ssavice.company.service.CompanyService;
import teamssavice.ssavice.company.service.dto.CompanyCommand;
import teamssavice.ssavice.company.service.dto.CompanyModel;
import teamssavice.ssavice.global.annotation.CurrentAuth;
import teamssavice.ssavice.global.annotation.PermitAll;
import teamssavice.ssavice.global.annotation.RequireRole;
import teamssavice.ssavice.global.dto.Auth;
import teamssavice.ssavice.imageresource.ImageRequest;
import teamssavice.ssavice.imageresource.ImageResponse;
import teamssavice.ssavice.imageresource.constants.ImageContentType;
import teamssavice.ssavice.imageresource.constants.ImagePath;
import teamssavice.ssavice.imageresource.service.ImageService;
import teamssavice.ssavice.imageresource.service.dto.ImageModel;
import teamssavice.ssavice.s3.S3Service;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/company")
@Validated
public class CompanyController {

    private final CompanyService companyService;
    private final ImageService imageService;
    private final S3Service s3Service;

    @PermitAll
    @PostMapping("/login")
    public ResponseEntity<CompanyResponse.Login> login(
        @RequestBody @Valid CompanyRequest.Login request
    ) {
        CompanyModel.Login model = companyService.login(request.token(), request.provider());
        return ResponseEntity.ok(CompanyResponse.Login.from(model));
    }

    @PostMapping
    @RequireRole(Role.USER)
    public ResponseEntity<CompanyResponse.Login> register(
        @CurrentAuth Auth authUser,
        @RequestBody @Valid CompanyRequest.Create request
    ) {
        CompanyModel.Login model = companyService.register(
            CompanyCommand.Create.from(authUser.id(), request));
        return ResponseEntity.ok(CompanyResponse.Login.from(model));
    }

    @PutMapping
    @RequireRole(Role.COMPANY)
    public ResponseEntity<CompanyResponse> putCompany(
        @CurrentAuth Auth authCompany,
        @RequestBody @Valid CompanyRequest.Update request
    ) {
        companyService.updateCompany(CompanyCommand.Update.from(authCompany.id(), request));
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @RequireRole(Role.COMPANY)
    public ResponseEntity<CompanyResponse.MyCompany> getCompany(
        @CurrentAuth Auth authCompany
    ) {
        CompanyModel.MyCompany model = companyService.getMyCompany(authCompany.id());
        return ResponseEntity.ok(CompanyResponse.MyCompany.from(model));
    }

    @PermitAll
    @GetMapping("/{company-id}")
    public ResponseEntity<CompanyResponse.Info> getCompanyById(
        @PathVariable("company-id") @Positive Long companyId
    ) {
        CompanyModel.Info model = companyService.getCompanyById(companyId);
        return ResponseEntity.ok(CompanyResponse.Info.from(model));
    }

    @PermitAll
    @GetMapping("/{company-id}/summary")
    public ResponseEntity<CompanyResponse.Summary> getCompanySummary(
        @PathVariable("company-id") @Positive Long companyId
    ) {
        CompanyModel.Summary model = companyService.getCompanySummary(companyId);
        return ResponseEntity.ok(CompanyResponse.Summary.from(model));
    }

    @PostMapping("/image")
    @RequireRole(Role.COMPANY)
    public ResponseEntity<ImageResponse.PresignedUrl> createCompanyPresignedUrl(
        @CurrentAuth Auth authCompany,
        @RequestBody @Valid ImageRequest.ContentType request
    ) {
        ImageModel.PutPresignedUrl model = imageService.updateImage(authCompany.id(), ImagePath.company,
            ImageContentType.from(request.contentType()));
        return ResponseEntity.ok(ImageResponse.PresignedUrl.from(model));
    }

    @PostMapping("/image/confirm")
    @RequireRole(Role.COMPANY)
    public ResponseEntity<Void> confirmCompanyImageUpload(
        @CurrentAuth Auth authCompany,
        @RequestBody @Valid ImageRequest.Confirm request
    ) {
        s3Service.validateTempImageOrDelete(request.objectKey());
        companyService.updateCompanyImage(authCompany.id(), request.objectKey());
        return ResponseEntity.ok().build();
    }


    @PostMapping("/validate")
    @RequireRole(Role.USER)
    public ResponseEntity<CompanyResponse.Validate> validateBusiness(
        @CurrentAuth Auth authUser,
        @RequestBody @Valid CompanyRequest.Validate request
    ) {
        CompanyModel.Validate model = companyService.validateBusinessNumber(authUser.id(),
            request.toCommand());
        return ResponseEntity.ok(CompanyResponse.Validate.from(model));
    }

    @GetMapping("/address")
    @RequireRole(Role.COMPANY)
    public ResponseEntity<AddressResponse.RegionDetail> getAddress(
            @CurrentAuth Auth authCompany
    ) {
        AddressModel.RegionDetail model = companyService.getCompanyAddress(authCompany.id());
        return ResponseEntity.ok(AddressResponse.RegionDetail.from(model));
    }
}
