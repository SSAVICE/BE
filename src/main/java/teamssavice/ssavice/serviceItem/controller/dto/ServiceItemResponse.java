package teamssavice.ssavice.serviceItem.controller.dto;

import teamssavice.ssavice.serviceItem.service.dto.ServiceItemModel;

public class ServiceItemResponse {
    public record Register(
            Long serviceId
    ) {
        public static Register from(ServiceItemModel.ItemInfo model) {
            return new Register(model.id());
        }
    }
}
