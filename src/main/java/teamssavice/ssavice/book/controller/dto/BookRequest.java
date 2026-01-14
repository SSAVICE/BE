package teamssavice.ssavice.book.controller.dto;

import teamssavice.ssavice.book.constants.BookStatus;

public class BookRequest {

    public enum BookStatusFilter {
        ALL,
        APPLYING,
        MATCHED,
        FAILED,
        CANCELED,
        COMPLETED;

        public BookStatus toDomainOrNull() {
            return this == ALL ? null : BookStatus.valueOf(this.name());
        }
    }
}
