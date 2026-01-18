package teamssavice.ssavice.refund.service;

import teamssavice.ssavice.book.entity.Book;
import teamssavice.ssavice.refund.entity.RefundInfo;
import teamssavice.ssavice.serviceItem.entity.Price;

import java.util.List;
import java.util.Map;

public interface RefundService {
    void registerRefunds(List<Book> targetBooks, Price price);

}
