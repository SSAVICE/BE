package teamssavice.ssavice.address.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import teamssavice.ssavice.address.Address;
import teamssavice.ssavice.address.AddressRepository;
import teamssavice.ssavice.address.service.dto.AddressCommand;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final AddressRepository addressRepository;

    public Address save(AddressCommand.Create command) {
        Address address = Address.builder()
                .latitude(command.latitude())
                .longitude(command.longitude())
                .postCode(command.postCode())
                .address(command.address())
                .detailAddress(command.detailAddress())
                .build();
        return addressRepository.save(address);
    }
}
