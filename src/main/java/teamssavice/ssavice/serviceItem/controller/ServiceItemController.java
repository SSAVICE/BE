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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/service")
public class ServiceItemController {

    private final ServiceItemService serviceItemService;

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
}
