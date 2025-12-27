package teamssavice.ssavice.fixture;

import teamssavice.ssavice.address.Address;
import teamssavice.ssavice.company.entity.Company;
import teamssavice.ssavice.serviceItem.entity.Price;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;

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
}
