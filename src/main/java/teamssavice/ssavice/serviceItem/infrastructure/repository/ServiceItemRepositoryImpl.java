package teamssavice.ssavice.serviceItem.infrastructure.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import teamssavice.ssavice.address.Address;
import teamssavice.ssavice.global.util.GeoHashUtil;
import teamssavice.ssavice.serviceItem.constants.ServiceCategory;
import teamssavice.ssavice.serviceItem.constants.ServiceStatus;
import teamssavice.ssavice.serviceItem.constants.ServiceStatusFilter;
import teamssavice.ssavice.serviceItem.constants.SortType;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;
import teamssavice.ssavice.serviceItem.service.dto.ServiceItemCommand;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
        int pageSize = command.pageable().getPageSize();
        boolean isDistanceSort = command.sortType() == SortType.DISTANCE;

        BooleanBuilder baseCondition = new BooleanBuilder();
        NumberExpression<Double> distanceExpr = null;

        OrderSpecifier<?>[] orderSpecifiers;

        if (isDistanceSort) {
            String geoHash = GeoHashUtil.encode(command.userLatitude(), command.userLongitude(), 5);
            baseCondition.and(buildGeoCondition(GeoHashUtil.getNeighbors(geoHash)));
            distanceExpr = haversineDistance(command.userLatitude(), command.userLongitude());
            baseCondition.and(distanceExpr.loe(2000.0));
            baseCondition.and(distanceCursorCondition(
                command.lastId(), command.userLatitude(), command.userLongitude(), distanceExpr));
            orderSpecifiers = new OrderSpecifier[]{distanceExpr.asc(), serviceItem.id.asc()};
        } else {
            baseCondition.and(ltLastId(command.lastId()));
            orderSpecifiers = getOrderSpecifier(command.sortType());
        }

        baseCondition
            .and(eqCategory(command.category()))
            .and(containsQuery(command.query()))
            .and(goeMinPrice(command.minPrice()))
            .and(loeMaxPrice(command.maxPrice()))
            .and(applyOnSaleCondition(now, command.onSale()));

        List<ServiceItem> content = queryFactory
            .selectFrom(serviceItem)
            .join(serviceItem.company, company).fetchJoin()
            .join(serviceItem.address, address1).fetchJoin()
            .leftJoin(serviceItem.thumbnailImageResource, imageResource).fetchJoin()
            .where(baseCondition)
            .orderBy(orderSpecifiers)
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

    @Override
    public Slice<ServiceItem> findNearbyByGeoHashes(
        BigDecimal latitude,
        BigDecimal longitude,
        BigDecimal userLatitude,
        BigDecimal userLongitude,
        int radiusMeters,
        List<String> geoHashes,
        int size,
        Long lastId
    ) {
        BooleanExpression geoCondition = buildGeoCondition(geoHashes);
        NumberExpression<Double> radiusDistanceExpr = haversineDistance(latitude, longitude);
        NumberExpression<Double> userDistanceExpr = haversineDistance(userLatitude, userLongitude);

        BooleanExpression baseCondition = geoCondition
            .and(applyOnSaleCondition(LocalDateTime.now(), true))
            .and(radiusDistanceExpr.loe((double) radiusMeters));

        BooleanExpression cursorCondition = distanceCursorCondition(lastId, userLatitude, userLongitude, userDistanceExpr);
        if (cursorCondition != null) {
            baseCondition = baseCondition.and(cursorCondition);
        }

        List<ServiceItem> content = queryFactory
            .selectFrom(serviceItem)
            .join(serviceItem.company, company).fetchJoin()
            .join(serviceItem.address, address1).fetchJoin()
            .leftJoin(serviceItem.thumbnailImageResource, imageResource).fetchJoin()
            .where(baseCondition)
            .orderBy(userDistanceExpr.asc(), serviceItem.id.asc())
            .limit(size + 1)
            .fetch();

        boolean hasNext = false;
        if (content.size() > size) {
            content.remove(size);
            hasNext = true;
        }

        return new SliceImpl<>(content, PageRequest.of(0, size), hasNext);
    }

    private BooleanExpression ltLastId(Long lastId) {
        return lastId == null ? null : serviceItem.id.lt(lastId);
    }

    private BooleanExpression eqCategory(ServiceCategory category) {
        if (category == null || category == ServiceCategory.ALL) return null;
        return serviceItem.category.eq(category);
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

    private OrderSpecifier<?>[] getOrderSpecifier(SortType sortType) {
        OrderSpecifier[] defaultSort = {serviceItem.createdAt.desc()};

        if (sortType == null) {
            return defaultSort;
        }

        return switch (sortType) {
            case PRICE_ASC -> new OrderSpecifier[]{serviceItem.price.discountedPrice.asc(), serviceItem.id.desc()};
            case PRICE_DESC -> new OrderSpecifier[]{serviceItem.price.discountedPrice.desc(), serviceItem.id.desc()};
            case DISCOUNT_RATE -> new OrderSpecifier[]{serviceItem.price.discountRate.desc(), serviceItem.id.desc()};
            default -> defaultSort;
        };
    }

    private BooleanExpression applyOnSaleCondition(LocalDateTime now, boolean onSale) {
        if (!onSale) return null;

        return serviceItem.status.eq(ServiceStatus.RECRUITING)
            .and(serviceItem.deadline.gt(now));
    }


    private BooleanExpression distanceCursorCondition(
        Long lastId, BigDecimal userLatitude, BigDecimal userLongitude,
        NumberExpression<Double> userDistanceExpr
    ) {
        if (lastId == null) {
            return null;
        }
        Address lastAddress = findAddressByServiceItemId(lastId).orElse(null);
        if (lastAddress == null) {
            return null;
        }
        double lastDistance = GeoHashUtil.calculateDistance(
            userLatitude, userLongitude,
            lastAddress.getLatitude(), lastAddress.getLongitude()
        );
        return userDistanceExpr.gt(lastDistance)
            .or(userDistanceExpr.eq(lastDistance).and(serviceItem.id.gt(lastId)));
    }

    private Optional<Address> findAddressByServiceItemId(Long serviceItemId) {
        return Optional.ofNullable(
            queryFactory
                .select(serviceItem.address)
                .from(serviceItem)
                .where(serviceItem.id.eq(serviceItemId))
                .fetchOne()
        );
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
