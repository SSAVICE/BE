package teamssavice.ssavice.company.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.address.AddressCommand;
import teamssavice.ssavice.address.AddressModel;
import teamssavice.ssavice.auth.Token;
import teamssavice.ssavice.auth.constants.Role;
import teamssavice.ssavice.auth.service.TokenService;
import teamssavice.ssavice.company.entity.Company;
import teamssavice.ssavice.company.infrastructure.dto.CompanyInfraCommand;
import teamssavice.ssavice.company.service.client.BusinessVerificationClient;
import teamssavice.ssavice.company.service.dto.CompanyCommand;
import teamssavice.ssavice.company.service.dto.CompanyModel;
import teamssavice.ssavice.company.token.CompanySignupVerifyToken;
import teamssavice.ssavice.company.token.CompanySignupVerifyTokenService;
import teamssavice.ssavice.imageresource.entity.ImageResource;
import teamssavice.ssavice.imageresource.service.ImageReadService;
import teamssavice.ssavice.oauth.service.OAuthReadService;
import teamssavice.ssavice.oauth.service.client.OAuthUserInfo;
import teamssavice.ssavice.region.Region;
import teamssavice.ssavice.region.RegionReadService;
import teamssavice.ssavice.review.entity.Review;
import teamssavice.ssavice.review.service.ReviewReadService;
import teamssavice.ssavice.s3.S3Service;
import teamssavice.ssavice.s3.event.S3EventDto;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;
import teamssavice.ssavice.serviceItem.service.ServiceItemReadService;
import teamssavice.ssavice.serviceItem.service.dto.ServiceItemModel;
import teamssavice.ssavice.user.constants.Provider;
import teamssavice.ssavice.user.entity.Users;
import teamssavice.ssavice.user.service.UserReadService;
import teamssavice.ssavice.user.service.UserWriteService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final TokenService tokenService;
    private final UserReadService userReadService;
    private final UserWriteService userWriteService;
    private final CompanyReadService companyReadService;
    private final CompanyWriteService companyWriteService;
    private final ServiceItemReadService serviceItemReadService;
    private final ReviewReadService reviewReadService;
    private final ImageReadService imageReadService;
    private final S3Service s3Service;
    private final BusinessVerificationClient businessVerificationClient;
    private final RegionReadService regionReadService;
    private final CompanySignupVerifyTokenService companySignupVerifyTokenService;
    private final OAuthReadService oAuthReadService;


    public CompanyModel.Login login(String oAuthToken, Provider provider) {
        OAuthUserInfo oAuthUserInfo = oAuthReadService.getUserInfo(provider, oAuthToken);

        Account account = companyWriteService.findOrCreateAccount(oAuthUserInfo, provider);

        Optional<Company> optionalCompany = companyReadService.findOptionalById(account.getId());
        if (optionalCompany.isEmpty()) {
            Token token = tokenService.issueToken(account.getId(), Role.COMPANY);
            return CompanyModel.Login.from(token, false);
        }

        Company company = optionalCompany.get();
        Token token = tokenService.issueToken(company.getId(), Role.COMPANY);
        return CompanyModel.Login.from(token, true);
    }

    @Transactional
    public CompanyModel.Login register(CompanyCommand.Create command) {
        companySignupVerifyTokenService.validate(
            command.accountId(), command.businessNumber(), command.startDate(), command.ownerName(), command.businessName(), command.verifyToken());

        Account account = accountRepository.findById(command.accountId())
            .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ACCOUNT_NOT_FOUND));
        companyReadService.checkAccountExists(account.getId());

        Region region = regionReadService.findByRegionCode(command.regionCode());
        Company company = companyWriteService.save(command, account,
            AddressCommand.RegionInfo.from(command, region));
        Token token = tokenService.issueToken(company.getId(), Role.COMPANY);
        return CompanyModel.Login.from(token, true);
    }

    @Transactional
    public void updateCompany(CompanyCommand.Update command) {
        Company company = companyReadService.findByCompanyIdFetchJoinAddress(command.companyId());
        company.update(command);
        if (command.regionCode() != null) {
            Region region = regionReadService.findByRegionCode(command.regionCode());
            company.getAddress().update(AddressCommand.RegionInfo.from(command, region));
        }
    }

    @Transactional(readOnly = true)
    public CompanyModel.MyCompany getMyCompany(Long id) {
        Company company = companyReadService.findByIdFetchJoinAddressAndImageResource(id);
        List<ServiceItem> services = serviceItemReadService.findTop5ByCompanyIdOrderByDeadlineDesc(
            company.getId());
        String presignedUrl = s3Service.generateGetPresignedUrl(company.getObjectKey());
        List<ServiceItemModel.Summary> serviceModels = services.stream()
            .map(serviceItem -> ServiceItemModel.Summary.from(serviceItem,
                s3Service.generateGetPresignedUrl(serviceItem.getObjectKey())))
            .toList();

        return CompanyModel.MyCompany.from(company, presignedUrl, serviceModels);
    }

    @Transactional(readOnly = true)
    public CompanyModel.Info getCompanyById(Long id) {
        Company company = companyReadService.findByIdFetchJoinAddressAndImageResource(id);
        List<ServiceItem> services = serviceItemReadService.findTop5ByCompanyIdOrderByDeadlineDesc(
            company.getId());
        List<Review> reviews = reviewReadService.findTop3ByCompanyIdOrderByCreatedAt(
            company.getId());
        String presignedUrl = s3Service.generateGetPresignedUrl(company.getObjectKey());
        List<ServiceItemModel.Summary> serviceModels = services.stream()
            .map(serviceItem -> ServiceItemModel.Summary.from(serviceItem,
                s3Service.generateGetPresignedUrl(serviceItem.getObjectKey())))
            .toList();

        return CompanyModel.Info.from(company, presignedUrl, serviceModels, reviews);
    }

    @Transactional(readOnly = true)
    public CompanyModel.Summary getCompanySummary(Long id) {
        Company company = companyReadService.findByIdFetchJoinAddressAndImageResource(id);
        List<Review> reviews = reviewReadService.findTop3ByCompanyIdOrderByCreatedAt(
            company.getId());

        String presignedUrl = s3Service.generateGetPresignedUrl(company.getObjectKey());
        return CompanyModel.Summary.from(company, presignedUrl, reviews);
    }

    @Transactional
    public void updateCompanyImage(Long companyId, String objectKey) {
        Company company = companyReadService.findByIdFetchJoinImageResource(companyId);
        ImageResource imageResource = imageReadService.findBySourceKey(objectKey);
        imageResource.checkedConfirmed();
        if (company.hasImageResource()) {
            applicationEventPublisher.publishEvent(
                S3EventDto.Delete.from(company.getImageResource())
            );
        }
        company.updateImage(imageResource);
        applicationEventPublisher.publishEvent(S3EventDto.Move.from(imageResource));
    }

    public CompanyModel.Validate validateBusinessNumber(Long userId,
                                                        CompanyCommand.Validate command) {
        CompanyInfraCommand.Validate infraCommand = command.toInfraCommand();
        businessVerificationClient.validate(infraCommand);
        CompanySignupVerifyToken verifyToken = companySignupVerifyTokenService.issueToken(
            userId, command.businessNumber(), command.startDate(), command.name(), command.businessName());

        return CompanyModel.Validate.from(verifyToken);
    }

    @Transactional(readOnly = true)
    public AddressModel.RegionDetail getCompanyAddress(Long companyId) {
        Company company = companyReadService.findByCompanyIdFetchJoinAddress(companyId);
        return AddressModel.RegionDetail.from(company.getAddress());
    }
}
