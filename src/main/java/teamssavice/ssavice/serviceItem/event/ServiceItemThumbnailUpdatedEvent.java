package teamssavice.ssavice.serviceItem.event;

public record ServiceItemThumbnailUpdatedEvent(
        Long serviceItemId,
        String thumbKey
) {}
