package teamssavice.ssavice.serviceItem.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import teamssavice.ssavice.auth.constants.Role;
import teamssavice.ssavice.global.annotation.CurrentId;
import teamssavice.ssavice.global.annotation.RequireRole;
import teamssavice.ssavice.global.dto.CursorResult;
import teamssavice.ssavice.serviceItem.controller.dto.ServiceItemRequest;
import teamssavice.ssavice.serviceItem.controller.dto.ServiceItemResponse;
import teamssavice.ssavice.serviceItem.service.ServiceItemService;
import teamssavice.ssavice.serviceItem.service.dto.ServiceItemCommand;
import teamssavice.ssavice.serviceItem.service.dto.ServiceItemModel;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/service")
public class ServiceItemController {

    private final ServiceItemService serviceItemService;

    @PostMapping
    @RequireRole(Role.COMPANY)
    public ResponseEntity<ServiceItemResponse.Register> createServiceItem(
            @CurrentId Long userId,
            @RequestBody @Valid ServiceItemRequest.Create request
    ) {

        ServiceItemCommand.Create command = ServiceItemCommand.Create.from(userId, request);
        ServiceItemModel.ItemInfo model = serviceItemService.register(command);

        return ResponseEntity.ok(ServiceItemResponse.Register.from(model));

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
}
