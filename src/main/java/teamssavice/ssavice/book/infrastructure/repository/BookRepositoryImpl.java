package teamssavice.ssavice.book.infrastructure.repository;

import static teamssavice.ssavice.address.QAddress.address1;
import static teamssavice.ssavice.book.entity.QBook.book;
import static teamssavice.ssavice.company.entity.QCompany.company;
import static teamssavice.ssavice.imageresource.entity.QImageResource.imageResource;
import static teamssavice.ssavice.serviceItem.entity.QServiceItem.serviceItem;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import teamssavice.ssavice.book.constants.BookStatusFilter;
import teamssavice.ssavice.book.entity.Book;
import teamssavice.ssavice.book.entity.BookStatus;
import teamssavice.ssavice.book.entity.QBook;
import teamssavice.ssavice.serviceItem.constants.ServiceStatus;

@RequiredArgsConstructor
public class BookRepositoryImpl implements BookRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Book> findAllByUserIdAndStatus(Long userId, BookStatusFilter status,
        Pageable pageable) {
        LocalDateTime now = LocalDateTime.now();
        BooleanExpression baseCondition = book.user.id.eq(userId);
        BooleanExpression statusCondition = statusCondition(status, now);

        List<Book> content = queryFactory
            .selectFrom(book)
            .join(book.serviceItem, serviceItem).fetchJoin()
            .join(book.serviceItem.company, company).fetchJoin()
            .join(book.serviceItem.address, address1).fetchJoin()
            .leftJoin(book.serviceItem.thumbnailImageResource, imageResource).fetchJoin()
            .where(
                baseCondition,
                statusCondition
            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(book.createdAt.desc())
            .fetch();

        Long total = queryFactory
            .select(book.count())
            .from(book)
            .where(
                baseCondition,
                statusCondition
            )
            .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }

    @Override
    public Page<Book> findAllByCompanyIdAndStatus(Long companyId, BookStatusFilter status,
        Pageable pageable) {
        LocalDateTime now = LocalDateTime.now();
        BooleanExpression baseCondition = book.serviceItem.company.id.eq(companyId);
        BooleanExpression statusCondition = statusCondition(status, now);

        List<Book> content = queryFactory
            .selectFrom(book)
            .join(book.serviceItem, serviceItem).fetchJoin()
            .join(book.serviceItem.company, company).fetchJoin()
            .join(book.serviceItem.address, address1).fetchJoin()
            .leftJoin(book.serviceItem.thumbnailImageResource, imageResource).fetchJoin()
            .where(
                baseCondition,
                statusCondition
            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(book.createdAt.desc())
            .fetch();

        Long total = queryFactory
            .select(book.count())
            .from(book)
            .where(
                baseCondition,
                statusCondition
            )
            .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }

    @Override
    public List<Book> findLatestBooksByUserIdAndServiceItemId(Long userId, List<Long> serviceItemIds) {
        return queryFactory
            .selectFrom(book)
            .where(
                book.user.id.eq(userId),
                book.serviceItem.id.in(serviceItemIds),
                book.id.in(latestBookIdsSubQuery(userId, serviceItemIds))
            )
            .fetch();
    }

    private BooleanExpression statusCondition(BookStatusFilter status, LocalDateTime now) {
        if (status == null || status == BookStatusFilter.ALL) {
            return null;
        }

        return switch (status) {
            case RECRUITING -> book.bookStatus.eq(BookStatus.RESERVED)
                .and(serviceItem.status.eq(ServiceStatus.RECRUITING))
                .and(serviceItem.deadline.gt(now));

            case CANCELED -> book.bookStatus.eq(BookStatus.CANCELED)
                .or(serviceItem.status.eq(ServiceStatus.CANCELED))
                .or(
                    serviceItem.status.eq(ServiceStatus.RECRUITING)
                        .and(serviceItem.deadline.loe(now))
                );

            case SUCCEEDED -> book.bookStatus.eq(BookStatus.RESERVED)
                .and(serviceItem.status.eq(ServiceStatus.SUCCEEDED))
                .and(serviceItem.endDate.gt(now));

            case COMPLETED -> book.bookStatus.eq(BookStatus.RESERVED)
                .and(serviceItem.status.eq(ServiceStatus.SUCCEEDED))
                .and(serviceItem.endDate.loe(now));
            default -> null;
        };
    }

    private JPQLQuery<Long> latestBookIdsSubQuery(Long userId, List<Long> serviceItemIds) {
        QBook bookSub = new QBook("bookSub");

        return JPAExpressions
            .select(bookSub.id.max())
            .from(bookSub)
            .where(
                    bookSub.user.id.eq(userId),
                    bookSub.serviceItem.id.in(serviceItemIds)
            )
            .groupBy(bookSub.serviceItem.id);
    }
}
