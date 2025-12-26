package teamssavice.ssavice.serviceItem.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import teamssavice.ssavice.auth.constants.Role;
import teamssavice.ssavice.global.annotation.CurrentId;
import teamssavice.ssavice.global.annotation.RequireRole;
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
}
