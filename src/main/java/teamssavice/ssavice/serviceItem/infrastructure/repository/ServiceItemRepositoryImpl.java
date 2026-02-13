package teamssavice.ssavice.serviceItem.infrastructure.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import teamssavice.ssavice.global.util.GeoHashUtil;
import teamssavice.ssavice.serviceItem.constants.ServiceStatus;
import teamssavice.ssavice.serviceItem.constants.ServiceStatusFilter;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;
import teamssavice.ssavice.serviceItem.service.dto.ServiceItemCommand;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static teamssavice.ssavice.address.QAddress.address1;
import static teamssavice.ssavice.company.entity.QCompany.company;
import static teamssavice.ssavice.imageresource.entity.QImageResource.imageResource;
import static teamssavice.ssavice.serviceItem.entity.QServiceItem.serviceItem;

@RequiredArgsConstructor
public class ServiceItemRepositoryImpl implements ServiceItemRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<ServiceItem> search(ServiceItemCommand.Search command) {
        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = command.pageable();
        int pageSize = pageable.getPageSize();

        List<ServiceItem> content = queryFactory
            .selectFrom(serviceItem)

            .join(serviceItem.company, company).fetchJoin()
            .join(serviceItem.address, address1).fetchJoin()
            .leftJoin(serviceItem.thumbnailImageResource, imageResource).fetchJoin()
            .where(
                ltLastId(command.lastId()),
                eqCategory(command.category()),
                containsQuery(command.query()),
                goeMinPrice(command.minPrice()),
                loeMaxPrice(command.maxPrice()),
                applyOnSaleCondition(now, command.onSale())
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

    @Override
    public Page<ServiceItem> findByCompanyAndStatus(Long companyId, ServiceStatusFilter status, Pageable pageable) {
        LocalDateTime now = LocalDateTime.now();
        BooleanExpression companyIdCondition = serviceItem.company.id.eq(companyId);
        BooleanExpression statusCondition = statusCondition(status, now);
        // Status 검증

        List<ServiceItem> content = queryFactory
            .selectFrom(serviceItem)
            .leftJoin(serviceItem.thumbnailImageResource, imageResource).fetchJoin()
            .where(
                companyIdCondition,
                statusCondition
            ).offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(serviceItem.createdAt.desc())
            .fetch();

        Long total = queryFactory
            .select(serviceItem.count())
            .from(serviceItem)
            .where(
                companyIdCondition,
                statusCondition
            ).fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
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
        // 기본값: createdAt 내림차순 (최신순)
        OrderSpecifier[] defaultSort = {serviceItem.createdAt.desc()};

        if (sortBy == null) {
            return defaultSort;
        }

        switch (sortBy) {
            case 1: // 가격 낮은 순
                return new OrderSpecifier[]{serviceItem.price.discountedPrice.asc(), serviceItem.id.desc()};
            case 2: // 가격 높은 순
                return new OrderSpecifier[]{serviceItem.price.discountedPrice.desc(), serviceItem.id.desc()};
            case 3: // 할인율 순
                return new OrderSpecifier[]{serviceItem.price.discountRate.desc(), serviceItem.id.desc()};
            default: // 인기순 하고 마감임박순은 아직 기준이 안정해져서 우선 최신순
                return defaultSort;
        }
    }

    private BooleanExpression applyOnSaleCondition(LocalDateTime now, boolean onSale) {
        if (!onSale) return null;

        return serviceItem.status.eq(ServiceStatus.RECRUITING)
            .and(serviceItem.deadline.gt(now));
    }

    @Override
    public Slice<ServiceItem> findNearbyByGeoHashes(
        BigDecimal latitude,
        BigDecimal longitude,
        BigDecimal userLatitude,
        BigDecimal userLongitude,
        int radiusMeters,
        List<String> geoHashes,
        int size
    ) {
        BooleanExpression geoCondition = buildGeoCondition(geoHashes);
        NumberExpression<Double> radiusDistanceExpr = haversineDistance(latitude, longitude);
        NumberExpression<Double> userDistanceExpr = haversineDistance(userLatitude, userLongitude);

        BooleanExpression baseCondition = geoCondition
            .and(applyOnSaleCondition(LocalDateTime.now(), true))
            .and(radiusDistanceExpr.loe((double) radiusMeters));

        List<ServiceItem> content = queryFactory
            .selectFrom(serviceItem)
            .join(serviceItem.company, company).fetchJoin()
            .join(serviceItem.address, address1).fetchJoin()
            .leftJoin(serviceItem.thumbnailImageResource, imageResource).fetchJoin()
            .where(baseCondition)
            .orderBy(userDistanceExpr.asc())
            .limit(size + 1)
            .fetch();

        boolean hasNext = false;
        if (content.size() > size) {
            content.remove(size);
            hasNext = true;
        }

        return new SliceImpl<>(content, PageRequest.of(0, size), hasNext);
    }

    private NumberExpression<Double> haversineDistance(BigDecimal latitude, BigDecimal longitude) {
        return Expressions.numberTemplate(Double.class,
            GeoHashUtil.EARTH_RADIUS_METERS + " * 2 * ASIN(SQRT(" +
                "POWER(SIN(RADIANS({0} - {1}) / 2), 2) + " +
                "COS(RADIANS({0})) * COS(RADIANS({1})) * " +
                "POWER(SIN(RADIANS({2} - {3}) / 2), 2)" +
                "))",
            latitude, address1.latitude,
            longitude, address1.longitude
        );
    }

    private BooleanExpression buildGeoCondition(List<String> geoHashes) {
        BooleanExpression condition = null;
        for (String hash : geoHashes) {
            BooleanExpression like = address1.geoHash.like(hash + "%");
            condition = (condition == null) ? like : condition.or(like);
        }
        return condition;
    }

    private BooleanExpression statusCondition(ServiceStatusFilter status, LocalDateTime now) {
        if (status == null || status == ServiceStatusFilter.ALL) {
            return null;
        }

        return switch (status) {
            case RECRUITING -> serviceItem.status.eq(ServiceStatus.RECRUITING)
                .and(serviceItem.deadline.gt(now));

            case CANCELED -> serviceItem.status.eq(ServiceStatus.CANCELED)
                .or(serviceItem.status.eq(ServiceStatus.RECRUITING).and(serviceItem.deadline.loe(now)));

            case SUCCEEDED -> serviceItem.status.eq(ServiceStatus.SUCCEEDED)
                .and(serviceItem.endDate.gt(now));

            case COMPLETED -> serviceItem.status.eq(ServiceStatus.SUCCEEDED)
                .and(serviceItem.endDate.loe(now));

            default -> null;
        };
    }
}
