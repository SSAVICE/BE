package teamssavice.ssavice.book.event;

import teamssavice.ssavice.serviceItem.infrastructure.opensearch.ServiceItemSearchDocument;

import java.util.Map;

public record BookChangedEvent(
        Long serviceItemId,
        Map<String, Object> partialFields
) {}