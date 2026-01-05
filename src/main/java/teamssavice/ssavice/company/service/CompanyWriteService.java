package teamssavice.ssavice.company.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.address.Address;
import teamssavice.ssavice.company.entity.Company;
import teamssavice.ssavice.company.infrastructure.repository.CompanyRepository;
import teamssavice.ssavice.company.service.dto.CompanyCommand;
import teamssavice.ssavice.imageresource.entity.ImageResource;
import teamssavice.ssavice.user.entity.Users;

@Service
@RequiredArgsConstructor
public class CompanyWriteService {
    private final CompanyRepository companyRepository;

    public Company save(CompanyCommand.Create command, Users user) {
        Address address = Address.builder()
                .latitude(command.latitude())
                .longitude(command.longitude())
                .postCode(command.postCode())
                .address(command.address())
                .detailAddress(command.detailAddress())
                .build();

        Company company = Company.builder()
                .companyName(command.companyName())
                .ownerName(command.ownerName())
                .phoneNumber(command.phoneNumber())
                .businessNumber(command.businessNumber())
                .description(command.description())
                .depositor(command.depositor())
                .accountNumber(command.accountNumber())
                .detail(command.detail())
                .address(address)
                .user(user)
                .build();

        return companyRepository.save(company);
    }

    @Transactional
    public void updateCompanyImage(Company company, ImageResource imageResource) {
        company.updateImage(imageResource);
    }
}
