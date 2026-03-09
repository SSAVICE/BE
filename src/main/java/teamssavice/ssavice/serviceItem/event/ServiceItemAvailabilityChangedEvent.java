package teamssavice.ssavice.serviceItem.event;

public record ServiceItemAvailabilityChangedEvent(
        Long serviceItemId,
        boolean isAvailable
) {}
