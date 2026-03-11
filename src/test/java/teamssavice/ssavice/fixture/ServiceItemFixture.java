package teamssavice.ssavice.fixture;

import teamssavice.ssavice.address.Address;
import teamssavice.ssavice.company.entity.Company;
import teamssavice.ssavice.global.util.GeoHashUtil;
import teamssavice.ssavice.serviceItem.constants.ServiceCategory;
import teamssavice.ssavice.serviceItem.constants.ServiceStatus;
import teamssavice.ssavice.serviceItem.entity.Price;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ServiceItemFixture {

    public static ServiceItem custom(String title, LocalDateTime deadline, Company company, Address address) {
        return ServiceItem.builder()
                .title(title)
                .description("this is desc")
                .price(Price.of(1000L, 10))
                .minimumMember(10L)
                .maximumMember(20L)
                .startDate(LocalDateTime.now().plusDays(10))
                .endDate(LocalDateTime.now().plusDays(30))
                .deadline(deadline)
                .category(ServiceCategory.FOOD)
                .company(company)
                .address(address)
                .build();
    }

    public static ServiceItem setCompany(Company company) {
        return ServiceItem.builder()
                .title("title")
                .description("this is desc")
                .price(Price.of(1000L, 10))
                .minimumMember(10L)
                .maximumMember(20L)
                .startDate(LocalDateTime.now().plusDays(10))
                .endDate(LocalDateTime.now().plusDays(30))
                .deadline(LocalDateTime.now().plusDays(5))
                .category(ServiceCategory.FOOD)
                .company(company)
                .address(Address.builder()
                        .gugun("gugun")
                        .gugunCode("gugunCode")
                        .region("region")
                        .regionCode("regionCode")
                        .latitude(BigDecimal.valueOf(33.333))
                        .longitude(BigDecimal.valueOf(33.333))
                        .geoHash(GeoHashUtil.encode(BigDecimal.valueOf(33.333), BigDecimal.valueOf(33.333)))
                        .postCode("postCode")
                        .address("address")
                        .detailAddress("detail")
                        .build())
                .build();
    }

    public static ServiceItem base(Company company) {
        return ServiceItem.builder()
                .title("title")
                .description("this is desc")
                .price(Price.of(1000L, 10))
                .currentMember(5L)
                .minimumMember(10L)
                .maximumMember(20L)
                .startDate(LocalDateTime.now().plusDays(10))
                .endDate(LocalDateTime.now().plusDays(30))
                .deadline(LocalDateTime.now().plusDays(5))
                .category(ServiceCategory.FOOD)
                .company(company)
                .address(Address.builder()
                        .gugun("gugun")
                        .gugunCode("gugunCode")
                        .region("region")
                        .regionCode("regionCode")
                        .latitude(BigDecimal.valueOf(33.333))
                        .longitude(BigDecimal.valueOf(33.333))
                        .geoHash(GeoHashUtil.encode(BigDecimal.valueOf(33.333), BigDecimal.valueOf(33.333)))
                        .postCode("postCode")
                        .address("address")
                        .detailAddress("detail")
                        .build())
                .build();
    }

    public static ServiceItem recruiting(Company company) {
        return base(company).toBuilder()
                .status(ServiceStatus.RECRUITING)
                .build();
    }

    public static ServiceItem succeeded(Company company) {
        return base(company).toBuilder()
                .currentMember(11L)
                .minimumMember(10L)
                .maximumMember(20L)
                .status(ServiceStatus.SUCCEEDED)
                .build();
    }

    public static ServiceItem fulled(Company company) {
        return base(company).toBuilder()
                .currentMember(20L)
                .minimumMember(10L)
                .maximumMember(20L)
                .status(ServiceStatus.SUCCEEDED)
                .build();
    }

    public static ServiceItem inUse(Company company) {
        return fulled(company).toBuilder()
                .deadline(LocalDateTime.now().minusDays(20))
                .startDate(LocalDateTime.now().minusDays(5))
                .endDate(LocalDateTime.now().plusDays(5))
                .status(ServiceStatus.SUCCEEDED)
                .build();
    }

    public static ServiceItem completed(Company company) {
        return fulled(company).toBuilder()
                .deadline(LocalDateTime.now().minusDays(20))
                .startDate(LocalDateTime.now().minusDays(5))
                .endDate(LocalDateTime.now().minusDays(1))
                .status(ServiceStatus.SUCCEEDED)
                .build();
    }

    public static ServiceItem failed(Company company) {
        return base(company).toBuilder()
                .deadline(LocalDateTime.now().minusDays(1))
                .status(ServiceStatus.RECRUITING)
                .build();
    }

    public static ServiceItem canceled(Company company) {
        return base(company).toBuilder()
                .isDeleted(true)
                .status(ServiceStatus.CANCELED)
                .build();
    }
}
