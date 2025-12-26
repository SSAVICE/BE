package teamssavice.ssavice.serviceItem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.address.Address;
import teamssavice.ssavice.company.entity.Company;
import teamssavice.ssavice.company.infrastructure.repository.CompanyRepository;
import teamssavice.ssavice.global.constants.ErrorCode;
import teamssavice.ssavice.global.exception.EntityNotFoundException;
import teamssavice.ssavice.serviceItem.entity.Price;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;
import teamssavice.ssavice.serviceItem.infrastructure.repository.ServiceItemRepository;
import teamssavice.ssavice.serviceItem.service.dto.ServiceItemCommand;
import teamssavice.ssavice.serviceItem.service.dto.ServiceItemModel;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ServiceItemService {

    private final ServiceItemRepository serviceItemRepository;
    private final CompanyRepository companyRepository;


    @Transactional
    public ServiceItemModel.ItemInfo register(ServiceItemCommand.Create command) {

        Company company = companyRepository.findByUserId(command.userId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.COMPANY_NOT_FOUND));

        Address address = Address.builder()
                .latitude(command.latitude())
                .longitude(command.longitude())
                .postCode(command.postCode())
                .address(command.address())
                .detailAddress(command.detailAddress()).build();


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

        ServiceItem savedServiceItem = serviceItemRepository.save(serviceItem);

        return ServiceItemModel.ItemInfo.from(savedServiceItem);
    }
}
