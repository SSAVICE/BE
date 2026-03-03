package teamssavice.ssavice.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.account.constants.Provider;
import teamssavice.ssavice.account.entity.Account;
import teamssavice.ssavice.account.infrastructure.repository.AccountRepository;
import teamssavice.ssavice.address.Address;
import teamssavice.ssavice.address.AddressCommand;
import teamssavice.ssavice.auth.constants.Role;
import teamssavice.ssavice.oauth.service.client.OAuthUserInfo;
import teamssavice.ssavice.region.Region;
import teamssavice.ssavice.user.constants.UserRole;
import teamssavice.ssavice.user.entity.Users;
import teamssavice.ssavice.user.infrastructure.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserWriteService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public Users findOrCreate(OAuthUserInfo oAuthUserInfo, Provider provider) {
        Account account = accountRepository.findByProviderIdAndProviderAndRole(oAuthUserInfo.providerId(), provider, Role.USER)
            .orElseGet(() -> accountRepository.save(Account.builder()
                .provider(provider)
                .providerId(oAuthUserInfo.providerId())
                .role(Role.USER)
                .build()));
        return userRepository.findById(account.getId())
            .orElseGet(() -> save(oAuthUserInfo, account));
    }

    @Transactional
    public Users save(OAuthUserInfo oAuthUserInfo, Account account) {
        Users user = Users.builder()
            .account(account)
            .userRole(UserRole.USER)
            .name(oAuthUserInfo.name())
            .email(oAuthUserInfo.email())
            .phoneNumber(oAuthUserInfo.phoneNumber())
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
