package teamssavice.ssavice.serviceItem.infrastructure.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;
import teamssavice.ssavice.serviceItem.service.dto.ServiceItemCommand;
import java.util.List;

import static teamssavice.ssavice.company.entity.QCompany.company;
import static teamssavice.ssavice.address.QAddress.address1;

import static teamssavice.ssavice.serviceItem.entity.QServiceItem.serviceItem;

@RequiredArgsConstructor
public class ServiceItemRepositoryImpl implements ServiceItemRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<ServiceItem>
    search(ServiceItemCommand.Search command) {

        Pageable pageable = command.pageable();
        int pageSize = pageable.getPageSize();

        List<ServiceItem> content = queryFactory
                .selectFrom(serviceItem)

                .join(serviceItem.company, company).fetchJoin()
                .join(serviceItem.address, address1).fetchJoin()
                .where(
                        ltLastId(command.lastId()),
                        eqCategory(command.category()),
                        containsQuery(command.query()),
                        goeMinPrice(command.minPrice()),
                        loeMaxPrice(command.maxPrice())
                )
                .orderBy(getOrderSpecifier(command.sortBy()))
                .limit(pageSize + 1)
                .fetch();

        boolean hasNext = false;
        if (content.size() > pageSize) {
            content.remove(pageSize);
            hasNext = true;
        }

        return new SliceImpl<>(content, PageRequest.of(0, pageSize), hasNext);

    }


    private BooleanExpression ltLastId(Long lastId) {
        return lastId == null ? null : serviceItem.id.lt(lastId);
    }

    private BooleanExpression eqCategory(String category) {
        return (category == null || category.isEmpty()) ? null : serviceItem.category.eq(category);
    }

    private BooleanExpression containsQuery(String query) {
        return (query == null || query.isEmpty()) ? null :
                serviceItem.title.contains(query).or(serviceItem.description.contains(query));
    }

    private BooleanExpression goeMinPrice(Long minPrice) {
        return minPrice != null ? serviceItem.price.discountedPrice.goe(minPrice) : null;
    }

    private BooleanExpression loeMaxPrice(Long maxPrice) {
        return maxPrice != null ? serviceItem.price.discountedPrice.loe(maxPrice) : null;
    }

    private OrderSpecifier<?>[] getOrderSpecifier(Integer sortBy) {
        // 기본값: ID 내림차 순 (최신순)
        OrderSpecifier[] defaultSort = { serviceItem.id.desc() };

        if (sortBy == null) {
            return defaultSort;
        }

        switch (sortBy) {
            case 1: // 가격 낮은 순
                return new OrderSpecifier[]{ serviceItem.price.discountedPrice.asc(), serviceItem.id.desc() };
            case 2: // 가격 높은 순
                return new OrderSpecifier[]{ serviceItem.price.discountedPrice.desc(), serviceItem.id.desc() };
            case 3: // 할인율 순
                return new OrderSpecifier[]{ serviceItem.price.discountRate.desc(), serviceItem.id.desc() };
            default: // 인기순 하고 마감임박순은 아직 기준이 안정해져서 우선 최신순
                return defaultSort;
        }
    }
}
