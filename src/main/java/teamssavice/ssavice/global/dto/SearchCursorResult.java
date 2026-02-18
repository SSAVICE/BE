package teamssavice.ssavice.global.dto;

import java.util.List;
import java.util.function.Function;

public record SearchCursorResult<T>(
        List<T> content,
        List<String> nextSearchAfter,
        boolean hasNext
) {
    public <U> SearchCursorResult<U> map(Function<T, U> function) {
        List<U> newContent = content.stream()
                .map(function)
                .toList();
        return new SearchCursorResult<>(newContent, nextSearchAfter, hasNext);
    }
}