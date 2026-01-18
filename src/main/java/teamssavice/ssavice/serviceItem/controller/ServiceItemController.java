package teamssavice.ssavice.serviceItem.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import teamssavice.ssavice.auth.constants.Role;
import teamssavice.ssavice.book.controller.dto.BookResponse;
import teamssavice.ssavice.book.service.dto.BookModel;
import teamssavice.ssavice.global.annotation.CurrentId;
import teamssavice.ssavice.global.annotation.RequireRole;
import teamssavice.ssavice.global.dto.CursorResult;
import teamssavice.ssavice.global.dto.PageResponse;
import teamssavice.ssavice.imageresource.ImageRequest;
import teamssavice.ssavice.imageresource.ImageResponse;
import teamssavice.ssavice.imageresource.constants.ImagePath;
import teamssavice.ssavice.imageresource.service.ImageService;
import teamssavice.ssavice.imageresource.service.dto.ImageModel;
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

    @PostMapping
    @RequireRole(Role.COMPANY)
    public ResponseEntity<ServiceItemResponse.Register> createServiceItem(
            @CurrentId Long companyId,
            @RequestBody @Valid ServiceItemRequest.Create request
    ) {
        Long serviceId = serviceItemService.register(request.toCommand(companyId));

        return ResponseEntity.ok(ServiceItemResponse.Register.from(serviceId));

    }

    @GetMapping("/search")
    public ResponseEntity<CursorResult<ServiceItemResponse.Search>> searchServiceItems(
            @ModelAttribute @Valid ServiceItemRequest.Search request,
            @RequestParam(defaultValue = "10") int size
    ) {

        Pageable pageable = PageRequest.of(0, size);
        CursorResult<ServiceItemModel.Search> models = serviceItemService.search(request.toCommand(pageable));

        CursorResult<ServiceItemResponse.Search> response = models.map(ServiceItemResponse.Search::from);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{serviceId}")
    public ResponseEntity<ServiceItemResponse.Detail> getServiceDetail(
            @PathVariable Long serviceId
    ) {

        ServiceItemModel.Detail model = serviceItemService.getServiceDetail(serviceId);

        return ResponseEntity.ok(ServiceItemResponse.Detail.from(model));
    }

    @PostMapping("/image")
    @RequireRole(Role.COMPANY)
    public ResponseEntity<ImageResponse.PresignedUrls> createServiceItemPresignedUrls(
            @CurrentId Long companyId,
            @RequestBody @Valid ImageRequest.ServiceImages request
    ) {
        List<ImageModel.PutPresignedUrl> models = imageService.updateImages(request.toCommand(companyId, ImagePath.serviceItem));
        return ResponseEntity.ok(ImageResponse.PresignedUrls.from(models));
    }

    @PostMapping("/{serviceId}/apply")
    @RequireRole(Role.USER)
    public ResponseEntity<BookResponse.Apply> applyServiceItem(
            @CurrentId Long userId,
            @PathVariable Long serviceId
    ) {
        BookModel.Apply model = serviceItemService.apply(userId, serviceId);

        return ResponseEntity.ok(BookResponse.Apply.from(model));
    }

    @GetMapping("/company/{company-id}")
    public ResponseEntity<PageResponse<ServiceItemResponse.Summary>> getCompanysServiceItems(
            @PathVariable("company-id") Long companyId,
            @PageableDefault(page = 0, size = 10) Pageable pageable,
            @RequestParam("on-sale") Boolean onSale
    ) {
        ServiceItemCommand.RetrieveByCompanyAndOnSale command = ServiceItemCommand.RetrieveByCompanyAndOnSale.of(companyId, pageable, onSale);

        Page<ServiceItemResponse.Summary> responses = serviceItemService.getServiceByCompanyAndStatus(command)
                .map(ServiceItemResponse.Summary::from);

        return ResponseEntity.ok(PageResponse.from(responses));
    }

    @DeleteMapping("/{serviceId}")
    @RequireRole(Role.COMPANY)
    public ResponseEntity<Void> deleteServiceItem(
            @CurrentId Long companyId,
            @PathVariable Long serviceId
    ) {
        serviceItemService.delete(ServiceItemCommand.Delete.of(companyId,serviceId));
        return ResponseEntity.noContent().build();
    }

}
