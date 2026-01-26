package teamssavice.ssavice.refund.service;

import teamssavice.ssavice.book.entity.Book;
import teamssavice.ssavice.refund.constants.RefundReason;
import teamssavice.ssavice.serviceItem.entity.Price;

import java.util.List;

public interface RefundService {
    void registerRefunds(List<Book> targetBooks, Price price, RefundReason refundReason);

}
