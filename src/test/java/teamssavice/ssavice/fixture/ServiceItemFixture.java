package teamssavice.ssavice.fixture;

import teamssavice.ssavice.address.Address;
import teamssavice.ssavice.company.entity.Company;
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
                .category("category")
                .company(company)
                .address(address)
                .build();
    }

    public static ServiceItem setCompanyAndStatus(Company company, ServiceStatus status) {
        return ServiceItem.builder()
                .title("title")
                .description("this is desc")
                .price(Price.of(1000L, 10))
                .minimumMember(10L)
                .maximumMember(20L)
                .startDate(LocalDateTime.now().plusDays(10))
                .endDate(LocalDateTime.now().plusDays(30))
                .deadline(LocalDateTime.now().plusDays(5))
                .category("category")
                .company(company)
                .address(Address.builder()
                        .gugun("gugun")
                        .gugunCode("gugunCode")
                        .region("region")
                        .regionCode("regionCode")
                        .latitude(BigDecimal.valueOf(33.333))
                        .longitude(BigDecimal.valueOf(33.333))
                        .postCode("postCode")
                        .address("address")
                        .detailAddress("detail")
                        .build())
                .status(status)
                .build();
    }
}
