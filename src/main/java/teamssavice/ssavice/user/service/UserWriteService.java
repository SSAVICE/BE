package teamssavice.ssavice.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.address.Address;
import teamssavice.ssavice.address.AddressCommand;
import teamssavice.ssavice.region.Region;
import teamssavice.ssavice.user.constants.Provider;
import teamssavice.ssavice.user.constants.UserRole;
import teamssavice.ssavice.user.entity.Users;
import teamssavice.ssavice.user.infrastructure.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserWriteService {
    private final UserRepository userRepository;

    @Transactional
    public Users save(String email) {
        Users user = Users.builder()
                .userRole(UserRole.USER)
                .provider(Provider.KAKAO)
                .name("홍길동")
                .email(email)
                .phoneNumber("010-1234-5678")
                .imageResource(null)
                .build();
        return userRepository.save(user);
    }

    @Transactional
    public void updateAddress(Users user, Region region, AddressCommand.Update command) {
        Address address = Address.builder()
                .gugun(region.getGugun())
                .gugunCode(region.getGugunCode())
                .region(region.getRegion())
                .regionCode(region.getRegionCode())
                .latitude(command.latitude())
                .longitude(command.longitude())
                .postCode(command.postCode())
                .address(command.address())
                .detailAddress(command.detailAddress())
                .build();
        user.updateAddress(address);
    }
}
