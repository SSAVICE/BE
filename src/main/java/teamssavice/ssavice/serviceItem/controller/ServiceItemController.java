package teamssavice.ssavice.serviceItem.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import teamssavice.ssavice.auth.constants.Role;
import teamssavice.ssavice.global.annotation.CurrentAuth;
import teamssavice.ssavice.global.annotation.PermitAll;
import teamssavice.ssavice.global.annotation.RequireRole;
import teamssavice.ssavice.global.dto.Auth;
import teamssavice.ssavice.global.dto.CursorResult;
import teamssavice.ssavice.global.dto.PageResponse;
import teamssavice.ssavice.imageresource.ImageRequest;
import teamssavice.ssavice.imageresource.ImageResponse;
import teamssavice.ssavice.imageresource.constants.ImagePath;
import teamssavice.ssavice.imageresource.service.ImageService;
import teamssavice.ssavice.imageresource.service.dto.ImageModel;
import teamssavice.ssavice.s3.S3Service;
import teamssavice.ssavice.serviceItem.constants.ServiceStatusFilter;
import teamssavice.ssavice.serviceItem.controller.dto.ServiceItemRequest;
import teamssavice.ssavice.serviceItem.controller.dto.ServiceItemResponse;
import teamssavice.ssavice.serviceItem.service.ServiceItemService;
import teamssavice.ssavice.serviceItem.service.dto.ServiceItemCommand;
import teamssavice.ssavice.serviceItem.service.dto.ServiceItemModel;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/service")
public class ServiceItemController {

    private final ServiceItemService serviceItemService;
    private final ImageService imageService;
    private final S3Service s3Service;

    @PostMapping
    @RequireRole(Role.COMPANY)
    public ResponseEntity<ServiceItemResponse.Register> createServiceItem(
            @CurrentAuth Auth authCompany,
            @RequestBody @Valid ServiceItemRequest.Create request
    ) {
        s3Service.validateAllTempImagesOrDeleteAll(request.toValidateCommand());
        Long serviceId = serviceItemService.register(request.toCommand(authCompany.id()));
        return ResponseEntity.ok(ServiceItemResponse.Register.from(serviceId));
    }

    @PermitAll
    @GetMapping("/search")
    public ResponseEntity<CursorResult<ServiceItemResponse.Search>> searchServiceItems(
        @ModelAttribute @Valid ServiceItemRequest.Search request,
        @RequestParam(defaultValue = "10") int size,
        @CurrentAuth(required = false) Auth authUser
    ) {
        CursorResult<ServiceItemModel.Search> models = serviceItemService.search(
                authUser.getIdIfCanAccess(Role.USER), request.toCommand(size));
        CursorResult<ServiceItemResponse.Search> response = models.map(ServiceItemResponse.Search::from);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/nearby")
    @RequireRole(Role.USER)
    public ResponseEntity<CursorResult<ServiceItemResponse.Nearby>> searchNearby(
        @ModelAttribute @Valid ServiceItemRequest.Nearby request,
        @RequestParam(defaultValue = "10") int size
    ) {
        CursorResult<ServiceItemModel.Nearby> models = serviceItemService.searchNearby(request.toCommand(size));
        CursorResult<ServiceItemResponse.Nearby> response = models.map(ServiceItemResponse.Nearby::from);
        return ResponseEntity.ok(response);
    }

    @PermitAll
    @GetMapping("/{serviceId}/summary")
    public ResponseEntity<ServiceItemResponse.Summary> getServiceItemSummary(
        @PathVariable Long serviceId
    ) {
        ServiceItemModel.Summary model = serviceItemService.getServiceItemSummary(serviceId);
        return ResponseEntity.ok(ServiceItemResponse.Summary.from(model));
    }

    @PermitAll
    @GetMapping("/{serviceId}")
    public ResponseEntity<ServiceItemResponse.Detail> getServiceDetail(
        @PathVariable Long serviceId,
        @CurrentAuth(required = false) Auth authUser
    ) {
        ServiceItemModel.Detail model = serviceItemService.getServiceDetail(serviceId, authUser.getIdIfCanAccess(Role.USER));
        return ResponseEntity.ok(ServiceItemResponse.Detail.from(model));
    }

    @PostMapping("/image")
    @RequireRole(Role.COMPANY)
    public ResponseEntity<ImageResponse.PresignedUrls> createServiceItemPresignedUrls(
        @CurrentAuth Auth authCompany,
        @RequestBody @Valid ImageRequest.ServiceImages request
    ) {
        List<ImageModel.PutPresignedUrl> models = imageService.updateImages(request.toCommand(authCompany.id(), ImagePath.serviceItem));
        return ResponseEntity.ok(ImageResponse.PresignedUrls.from(models));
    }

    @PermitAll
    @GetMapping("/company/{company-id}")
    public ResponseEntity<PageResponse<ServiceItemResponse.Summary>> getCompanysServiceItems(
        @PathVariable("company-id") Long companyId,
        @PageableDefault(page = 0, size = 10) Pageable pageable,
        @RequestParam("on-sale") Boolean onSale
    ) {
        ServiceItemCommand.RetrieveByCompanyAndOnSale command = ServiceItemCommand.RetrieveByCompanyAndOnSale.of(companyId, pageable, onSale);

        Page<ServiceItemResponse.Summary> responses = serviceItemService.getServiceItemByCompanyAndOnSale(command)
            .map(ServiceItemResponse.Summary::from);

        return ResponseEntity.ok(PageResponse.from(responses));
    }

    @GetMapping("/company/my")
    @RequireRole(Role.COMPANY)
    public ResponseEntity<PageResponse<ServiceItemResponse.Summary>> getMyCompanysServiceItems(
        @CurrentAuth Auth authCompany,
        @PageableDefault(page = 0, size = 10) Pageable pageable,
        @RequestParam("status") ServiceStatusFilter status
    ) {
        ServiceItemCommand.RetrieveByCompanyAndStatus command = ServiceItemCommand.RetrieveByCompanyAndStatus.of(authCompany.id(), pageable, status);

        Page<ServiceItemResponse.Summary> responses = serviceItemService.getServiceItemByCompanyAndStatus(command)
            .map(ServiceItemResponse.Summary::from);

        return ResponseEntity.ok(PageResponse.from(responses));
    }

    @GetMapping("/company/summary")
    @RequireRole(Role.COMPANY)
    public ResponseEntity<ServiceItemResponse.Count> getCompanysServiceItemCount(
        @CurrentAuth Auth authCompany
    ) {
        ServiceItemModel.Count model = serviceItemService.getCompanysServiceItemCount(authCompany.id());
        return ResponseEntity.ok(ServiceItemResponse.Count.from(model));
    }

    @DeleteMapping("/{serviceId}")
    @RequireRole(Role.COMPANY)
    public ResponseEntity<Void> deleteServiceItem(
        @CurrentAuth Auth authCompany,
        @PathVariable Long serviceId
    ) {
        serviceItemService.delete(ServiceItemCommand.Delete.of(authCompany.id(), serviceId));
        return ResponseEntity.noContent().build();
    }
}