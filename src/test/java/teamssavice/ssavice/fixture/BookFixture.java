package teamssavice.ssavice.fixture;

import teamssavice.ssavice.book.entity.BookStatus;
import teamssavice.ssavice.book.entity.Book;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;
import teamssavice.ssavice.user.entity.Users;

public class BookFixture {

    public static Book book(Users user, ServiceItem serviceItem, BookStatus status) {
        Book book = Book.builder()
                .user(user)
                .serviceItem(serviceItem)
                .bookStatus(status)
                .isReviewed(false)
                .build();

        return book;
    }
}
