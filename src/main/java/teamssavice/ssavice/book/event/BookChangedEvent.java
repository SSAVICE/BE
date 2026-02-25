package teamssavice.ssavice.book.event;

import teamssavice.ssavice.serviceItem.infrastructure.opensearch.ServiceItemSearchDocument;

public record BookChangedEvent(
        Long serviceItemId,
        ServiceItemSearchDocument document
) {}
