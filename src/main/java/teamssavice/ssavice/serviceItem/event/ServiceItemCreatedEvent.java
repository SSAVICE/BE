package teamssavice.ssavice.serviceItem.event;

import teamssavice.ssavice.serviceItem.infrastructure.opensearch.ServiceItemSearchDocument;

public record ServiceItemCreatedEvent(Long serviceItemId, ServiceItemSearchDocument document) {}