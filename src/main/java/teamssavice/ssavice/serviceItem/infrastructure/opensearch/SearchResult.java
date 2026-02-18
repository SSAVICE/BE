package teamssavice.ssavice.serviceItem.infrastructure.opensearch;

import org.opensearch.client.opensearch._types.FieldValue;

import java.util.List;

public record SearchResult(
        List<Item> items,
        List<String> nextSearchAfter,
        boolean hasNext
) {
    public record Item(
            ServiceItemSearchDocument document,
            List<String> sortValues,
            Double distanceKm
    ) {
    }
}