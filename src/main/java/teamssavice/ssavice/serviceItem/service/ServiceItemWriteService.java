package teamssavice.ssavice.serviceItem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.address.Address;
import teamssavice.ssavice.address.AddressCommand;
import teamssavice.ssavice.company.entity.Company;
import teamssavice.ssavice.serviceItem.entity.Price;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;
import teamssavice.ssavice.serviceItem.infrastructure.repository.ServiceItemRepository;
import teamssavice.ssavice.serviceItem.service.dto.ServiceItemCommand;

@Service
@RequiredArgsConstructor
@Transactional
public class ServiceItemWriteService {
    private final ServiceItemRepository serviceItemRepository;

    @Transactional
    public ServiceItem save(ServiceItemCommand.Create command, Company company, AddressCommand.RegionInfo addressCommand) {
        Address address = Address.builder()
                .gugun(addressCommand.gugun())
                .gugunCode(addressCommand.gugunCode())
                .region(addressCommand.region())
                .regionCode(addressCommand.regionCode())
                .latitude(command.latitude())
                .longitude(command.longitude())
                .postCode(command.postCode())
                .address(command.address())
                .detailAddress(command.detailAddress())
                .build();

        ServiceItem serviceItem = ServiceItem.builder()
                .company(company)
                .address(address)
                .title(command.title())
                .description(command.description())
                .price(Price.of(command.basePrice(), command.discountRate())) // 가격 객체 생성
                .minimumMember(command.minimumMember())
                .maximumMember(command.maximumMember())
                .startDate(command.startDate())
                .endDate(command.endDate())
                .deadline(command.deadline())
                .category(command.category())
                .tag(command.tag())
                .build();

        return serviceItemRepository.save(serviceItem);
    }
}
