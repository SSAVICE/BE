package teamssavice.ssavice.global.dto;

import java.util.List;
import java.util.function.Function;

public record CursorResult<T> (
        List<T> content,
        Long nextCursor,
        boolean hasNext) {

    public <U> CursorResult<U> map(Function<T, U> function) {

        List<U> newContent = content.stream()
                .map(function)
                .toList();

        return new CursorResult<>(newContent, nextCursor, hasNext);
    }
}
