package teamssavice.ssavice.serviceItem.infrastructure.opensearch;


import java.util.List;

public record SearchResult(
        List<Item> items,
        List<String> nextSearchAfter,
        boolean hasNext
) {
    public record Item(
            ServiceItemSearchDocument document,
            Double distanceKm
    ) {
    }
}