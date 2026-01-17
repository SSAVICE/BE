package teamssavice.ssavice.book.infrastructure.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import teamssavice.ssavice.book.constants.BookStatusFilter;
import teamssavice.ssavice.book.entity.Book;
import teamssavice.ssavice.book.entity.BookStatus;

import java.time.LocalDateTime;
import java.util.List;

import static teamssavice.ssavice.address.QAddress.address1;
import static teamssavice.ssavice.book.entity.QBook.book;
import static teamssavice.ssavice.company.entity.QCompany.company;
import static teamssavice.ssavice.serviceItem.entity.QServiceItem.serviceItem;

@RequiredArgsConstructor
public class BookRepositoryImpl implements BookRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Book> findAllByUserIdAndStatus(Long userId, BookStatusFilter status, Pageable pageable) {
        LocalDateTime now = LocalDateTime.now();
        BooleanExpression baseCondition = book.user.id.eq(userId);
        BooleanExpression statusCondition = statusCondition(status, now);

        List<Book> content = queryFactory
                .selectFrom(book)
                .join(book.serviceItem, serviceItem).fetchJoin()
                .join(book.serviceItem.company, company).fetchJoin()
                .join(book.serviceItem.address, address1).fetchJoin()
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

    private BooleanExpression statusCondition(BookStatusFilter status, LocalDateTime now) {
        if (status == null || status == BookStatusFilter.ALL) {
            return null;
        }

        return switch (status) {
            case RECRUITING -> serviceItem.isDeleted.isFalse()
                    .and(book.bookStatus.eq(BookStatus.RESERVED))
                    .and(serviceItem.currentMember.lt(serviceItem.maximumMember))
                    .and(serviceItem.deadline.gt(now));

            case CANCELED -> serviceItem.isDeleted.isTrue()
                    .or(book.bookStatus.eq(BookStatus.CANCELED))
                    .or(
                        serviceItem.deadline.lt(now)
                            .and(serviceItem.currentMember.lt(serviceItem.minimumMember))
                    );

            case COMPLETED -> serviceItem.isDeleted.isFalse()
                    .and(book.bookStatus.eq(BookStatus.RESERVED))
                    .and(
                        serviceItem.currentMember.goe(serviceItem.maximumMember)
                            .or(serviceItem.currentMember.goe(serviceItem.minimumMember)
                                .and(serviceItem.deadline.lt(now))
                            )
                    );

            default -> null;
        };
    }
}
