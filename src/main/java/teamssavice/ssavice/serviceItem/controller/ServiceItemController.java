package teamssavice.ssavice.serviceItem.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import teamssavice.ssavice.auth.constants.Role;
import teamssavice.ssavice.global.annotation.CurrentId;
import teamssavice.ssavice.global.annotation.RequireRole;
import teamssavice.ssavice.global.dto.CursorResult;
import teamssavice.ssavice.imageresource.ImageRequest;
import teamssavice.ssavice.imageresource.ImageResponse;
import teamssavice.ssavice.imageresource.constants.ImagePath;
import teamssavice.ssavice.imageresource.service.ImageService;
import teamssavice.ssavice.imageresource.service.dto.ImageCommand;
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

        ServiceItemCommand.Create command = ServiceItemCommand.Create.from(companyId, request);
        Long serviceId = serviceItemService.register(command);

        return ResponseEntity.ok(ServiceItemResponse.Register.from(serviceId));

    }

    @GetMapping("/search")
    public ResponseEntity<CursorResult<ServiceItemResponse.Search>> searchServiceItems(
            @ModelAttribute @Valid ServiceItemRequest.Search request,
            @RequestParam(defaultValue = "10") int size
    ) {

        Pageable pageable = PageRequest.of(0, size);
        ServiceItemCommand.Search command = ServiceItemCommand.Search.of(request, pageable);

        CursorResult<ServiceItemModel.ItemInfo> models = serviceItemService.search(command);

        CursorResult<ServiceItemResponse.Search> response = models.map(ServiceItemResponse.Search::from);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{serviceId}")
    public ResponseEntity<ServiceItemResponse.Detail> getServiceDetail(
            @PathVariable Long serviceId
    ) {

        ServiceItemModel.ItemInfo model = serviceItemService.getServiceDetail(serviceId);

        return ResponseEntity.ok(ServiceItemResponse.Detail.from(model));
    }

    @PostMapping("/image")
    @RequireRole(Role.COMPANY)
    public ResponseEntity<ImageResponse.PresignedUrls> createServiceItemPresignedUrls(
            @CurrentId Long companyId,
            @RequestBody @Valid ImageRequest.ServiceImages request
    ) {
        List<ImageModel.PutPresignedUrl> models = imageService.updateImages(ImageCommand.PutPresignedUrls.from(companyId, ImagePath.serviceItem, request));
        return ResponseEntity.ok(ImageResponse.PresignedUrls.from(models));
    }
}
