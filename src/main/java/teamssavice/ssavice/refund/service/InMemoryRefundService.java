package teamssavice.ssavice.refund.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import teamssavice.ssavice.book.entity.Book;
import teamssavice.ssavice.refund.entity.RefundInfo;
import teamssavice.ssavice.serviceItem.entity.Price;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class InMemoryRefundService implements RefundService {

    // 임시로 만들어둠 - 나중에 결제 붙이면서 다 수정
    private final Map<Long, RefundInfo> refundStorage = new ConcurrentHashMap<>();

    @Override
    public void registerRefunds(List<Book> canceledBooks, Price price) {
        for (Book book : canceledBooks) {
            RefundInfo info = RefundInfo.of(
                    book.getId(),
                    book.getUser().getId(),
                    price.getDiscountedPrice(),
                    "SERVICE_DELETED"
            );

            refundStorage.put(book.getId(), info);
            log.info("환불 정보 기록 완료: 예약ID={}, 유저ID={}, 환불 해줄 금액={}",
                    book.getId(), book.getUser().getId(), price.getDiscountedPrice());
        }
    }

}
