    package teamssavice.ssavice.serviceItem.infrastructure.opensearch;

    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.opensearch.client.json.JsonData;
    import org.opensearch.client.opensearch.OpenSearchClient;
    import org.opensearch.client.opensearch._types.DistanceUnit;
    import org.opensearch.client.opensearch._types.FieldValue;
    import org.opensearch.client.opensearch._types.SortOptions;
    import org.opensearch.client.opensearch._types.SortOrder;
    import org.opensearch.client.opensearch._types.query_dsl.BoolQuery;
    import org.opensearch.client.opensearch._types.query_dsl.Query;
    import org.opensearch.client.opensearch.core.SearchRequest;
    import org.opensearch.client.opensearch.core.SearchResponse;
    import org.opensearch.client.opensearch.core.search.Hit;
    import org.springframework.stereotype.Component;
    import teamssavice.ssavice.global.constants.ErrorCode;
    import teamssavice.ssavice.global.exception.ExternalApiException;
    import teamssavice.ssavice.serviceItem.constants.ServiceCategory;
    import teamssavice.ssavice.serviceItem.constants.SortType;
    import teamssavice.ssavice.serviceItem.service.dto.ServiceItemCommand;

    import java.io.IOException;
    import java.time.LocalDateTime;
    import java.time.format.DateTimeFormatter;
    import java.util.ArrayList;
    import java.util.List;

    @Component
    @RequiredArgsConstructor
    @Slf4j
    public class ServiceItemSearchClient {

        private static final String INDEX_NAME = "service-items";
        private final OpenSearchClient openSearchClient;

        public SearchResult search(ServiceItemCommand.Search command) {
            try {
                SearchRequest request = buildSearchRequest(command);
                SearchResponse<ServiceItemSearchDocument> response =
                        openSearchClient.search(request, ServiceItemSearchDocument.class);

                return toSearchResult(response, command.pageable().getPageSize(), command.sortType());
            } catch (IOException e) {
                log.error("OpenSearch 검색 실패", e);
                throw new ExternalApiException(ErrorCode.OPENSEARCH_SEARCH_FAILED);
            }
        }

        private SearchRequest buildSearchRequest(ServiceItemCommand.Search command) {
            int size = command.pageable().getPageSize();

            SearchRequest.Builder builder = new SearchRequest.Builder()
                    .index(INDEX_NAME)
                    .query(buildQuery(command))
                    .size(size + 1)
                    .sort(buildSort(command));

            if (command.searchAfter() != null && !command.searchAfter().isEmpty()) {
                builder.searchAfter(buildSearchAfter(command.searchAfter(), command.sortType()));
            }

            return builder.build();
        }

        private List<FieldValue> buildSearchAfter(List<String> searchAfter, SortType sortType) {
            FieldValue first = switch (sortType) {
                case PRICE_ASC, PRICE_DESC, DISCOUNT_RATE -> FieldValue.of(Long.parseLong(searchAfter.get(0)));
                case DISTANCE -> FieldValue.of(Double.parseDouble(searchAfter.get(0)));
                case LATEST -> FieldValue.of(searchAfter.get(0)); // 날짜는 String
            };
            FieldValue second = FieldValue.of(Long.parseLong(searchAfter.get(1))); // id는 항상 Long
            return List.of(first, second);
        }


        private Query buildQuery(ServiceItemCommand.Search command) {
            BoolQuery.Builder bool = new BoolQuery.Builder();

            // 1. (삭제되지 않은 문서)
            bool.filter(m -> m.term(t -> t.field("isDeleted").value(FieldValue.of(false))));

            // 2. 키워드 필터
            if (command.query() != null && !command.query().isBlank()) {
                bool.filter(m -> m.bool(b -> b
                        .should(s -> s.match(mm -> mm
                                .field("title")
                                .query(q -> q.stringValue(command.query()))
                        ))
                        .should(s -> s.term(t -> t
                                .field("tags")
                                .value(FieldValue.of(command.query()))
                        ))
                        .minimumShouldMatch("1")
                ));
            }

            // 3. 카테고리 필터
            if (command.category() != ServiceCategory.ALL) {
                bool.filter(m -> m.term(t -> t.field("category").value(FieldValue.of(command.category().name()))));
            }

            // 4. 지역 및 거리 필터
            switch (command.range()) {
                case GUGUN -> {
                    if (command.gugun() != null && !command.gugun().isBlank()) {
                        bool.filter(m -> m.term(t -> t.field("gugun").value(FieldValue.of(command.gugun()))));
                    }
                }
                case DONG -> {
                    if (command.region() != null && !command.region().isBlank()) {
                        bool.filter(m -> m.term(t -> t.field("region").value(FieldValue.of(command.region()))));
                    }
                }
                case KM_1_5 -> applyGeoFilter(bool, command, "1.5km");
                case KM_3   -> applyGeoFilter(bool, command, "3km");
            }

            // 5. 가격 범위
            if (command.minPrice() != null || command.maxPrice() != null) {
                bool.filter(m -> m.range(r -> {
                    r.field("discountedPrice");
                    if (command.minPrice() != null) r.gte(JsonData.of(command.minPrice()));
                    if (command.maxPrice() != null) r.lte(JsonData.of(command.maxPrice()));
                    return r;
                }));
            }

            // 6. 판매중 필터
            if (command.onSale()) {
//                String now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
                bool.filter(m -> m.terms(t -> t
                        .field("status")
                        .terms(v -> v.value(List.of(
                                FieldValue.of("RECRUITING"),
                                FieldValue.of("SUCCEEDED")
                        )))
                ));
                bool.filter(m -> m.range(r -> r.field("deadline").gt(JsonData.of(now))));
                bool.filter(m -> m.term(t -> t.field("isAvailable").value(FieldValue.of(true))));
            }
            return new Query.Builder().bool(bool.build()).build();
        }

        private List<SortOptions> buildSort(ServiceItemCommand.Search command) {
            List<SortOptions> sorts = new ArrayList<>();

            SortType sortType = command.sortType();

            switch (sortType) {
                case DISTANCE -> sorts.add(SortOptions.of(s -> s.geoDistance(g -> g
                        .field("location")
                        .location(l -> l.latlon(ll -> ll
                                .lat(command.userLatitude().doubleValue())
                                .lon(command.userLongitude().doubleValue())
                        ))
                        .order(SortOrder.Asc)
                        .unit(DistanceUnit.Kilometers)
                )));
                case PRICE_ASC -> sorts.add(SortOptions.of(s -> s.field(f -> f.field("discountedPrice").order(SortOrder.Asc))));
                case PRICE_DESC -> sorts.add(SortOptions.of(s -> s.field(f -> f.field("discountedPrice").order(SortOrder.Desc))));
                case DISCOUNT_RATE -> sorts.add(SortOptions.of(s -> s.field(f -> f.field("discountRate").order(SortOrder.Desc))));
                case LATEST -> sorts.add(SortOptions.of(s -> s.field(f -> f.field("createdAt").order(SortOrder.Desc))));
            }

            // id는 항상 보조 정렬
            sorts.add(SortOptions.of(s -> s.field(f -> f.field("id").order(SortOrder.Asc))));

            return sorts;
        }



        private SearchResult toSearchResult(SearchResponse<ServiceItemSearchDocument> response, int size, SortType sortType) {
            List<Hit<ServiceItemSearchDocument>> hits = response.hits().hits();

            boolean hasNext = hits.size() > size;
            List<Hit<ServiceItemSearchDocument>> resultHits = hasNext ? hits.subList(0, size) : hits;

            List<SearchResult.Item> items = resultHits.stream()
                    .map(hit -> new SearchResult.Item(
                            hit.source(),
                            extractDistance(hit, sortType)
                    ))
                    .toList();

            List<String> nextSearchAfter = null;
            if (hasNext && !resultHits.isEmpty()) {
                nextSearchAfter = resultHits.getLast().sort().stream()
                        .map(FieldValue::_toJsonString)
                        .toList();
            }

            return new SearchResult(items, nextSearchAfter, hasNext);
        }

        private void applyGeoFilter(BoolQuery.Builder bool, ServiceItemCommand.Search command, String distance) {
            bool.filter(m -> m.geoDistance(g -> g
                    .field("location")
                    .location(l -> l.latlon(ll -> ll
                            .lat(command.userLatitude().doubleValue())
                            .lon(command.userLongitude().doubleValue())
                    ))
                    .distance(distance)
            ));
        }

        private Double extractDistance(Hit<ServiceItemSearchDocument> hit, SortType sortType) {
            // 거리순 정렬일 때 sort 첫 번째 값이 거리(km)
            if (sortType == SortType.DISTANCE && !hit.sort().isEmpty()) {
                try {
                    return Double.parseDouble(String.valueOf(hit.sort().getFirst()));
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        }
    }