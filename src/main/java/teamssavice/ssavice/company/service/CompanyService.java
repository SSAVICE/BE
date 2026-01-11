package teamssavice.ssavice.company.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.auth.Token;
import teamssavice.ssavice.auth.constants.Role;
import teamssavice.ssavice.auth.service.TokenService;
import teamssavice.ssavice.company.entity.Company;
import teamssavice.ssavice.company.infrastructure.dto.CompanyInfraCommand;
import teamssavice.ssavice.company.infrastructure.dto.CompanyInfraModel;
import teamssavice.ssavice.company.service.client.BusinessVerificationClient;
import teamssavice.ssavice.company.service.dto.CompanyCommand;
import teamssavice.ssavice.company.service.dto.CompanyModel;
import teamssavice.ssavice.company.token.CompanySignupVerifyToken;
import teamssavice.ssavice.company.token.CompanySignupVerifyTokenService;
import teamssavice.ssavice.global.constants.ErrorCode;
import teamssavice.ssavice.global.exception.InvalidBusinessNumberException;
import teamssavice.ssavice.imageresource.constants.ImageConstants;
import teamssavice.ssavice.imageresource.entity.ImageResource;
import teamssavice.ssavice.imageresource.service.ImageReadService;
import teamssavice.ssavice.review.entity.Review;
import teamssavice.ssavice.review.service.ReviewReadService;
import teamssavice.ssavice.s3.S3Service;
import teamssavice.ssavice.s3.event.S3EventDto;
import teamssavice.ssavice.serviceItem.entity.ServiceItem;
import teamssavice.ssavice.serviceItem.service.ServiceItemReadService;
import teamssavice.ssavice.user.entity.Users;
import teamssavice.ssavice.user.service.UserReadService;
import teamssavice.ssavice.user.service.UserWriteService;

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
    private final CompanySignupVerifyTokenService companySignupVerifyTokenService;


    public CompanyModel.Login login(String kakaoToken) {
        // 토큰 검증
        String email = "company@test.com";

        // user 저장 및 중복 체크
        Users user = userReadService.findByEmail(email)
            .orElseGet(() -> userWriteService.save(email));

        // 업체가 있으면 업체 토큰, 없으면 유저 토큰
        Optional<Company> optionalCompany = companyReadService.findByUser(user);
        if (optionalCompany.isEmpty()) {
            Token token = tokenService.issueToken(user.getId(), Role.USER);
            return CompanyModel.Login.from(token, false);
        }

        Company company = optionalCompany.get();
        Token token = tokenService.issueToken(company.getId(), Role.COMPANY);
        return CompanyModel.Login.from(token, true);
    }

    public CompanyModel.Login register(CompanyCommand.Create command) {
        companySignupVerifyTokenService.validate(
            command.userId(), command.businessNumber(), command.verifyToken());

        Users user = userReadService.findById(command.userId());
        companyReadService.checkUserExists(user);

        Company company = companyWriteService.save(command, user);
        Token token = tokenService.issueToken(company.getId(), Role.COMPANY);
        return CompanyModel.Login.from(token, true);
    }

    @Transactional
    public void updateCompany(CompanyCommand.Update command) {
        Company company = companyReadService.findByCompanyIdFetchJoinAddress(command.companyId());
        company.update(command);
    }

    @Transactional(readOnly = true)
    public CompanyModel.MyCompany getMyCompany(Long id) {
        Company company = companyReadService.findByIdFetchJoinAddressAndImageResource(id);
        List<ServiceItem> services = serviceItemReadService.findTop5ByCompanyOrderByDeadlineDesc(
            company);
        if (company.hasImageResource()) {
            String presignedUrl = s3Service.generateGetPresignedUrl(
                company.getImageResource().getObjectKey());
            return CompanyModel.MyCompany.from(company, presignedUrl, services);
        }
        return CompanyModel.MyCompany.from(company, ImageConstants.DEFAULT_COMPANY_IMAGE_OBJECT_KEY,
            services);
    }

    @Transactional(readOnly = true)
    public CompanyModel.Info getCompanyById(Long id) {
        Company company = companyReadService.findByIdFetchJoinAddressAndImageResource(id);
        List<ServiceItem> services = serviceItemReadService.findTop5ByCompanyOrderByDeadlineDesc(
            company);
        List<Review> reviews = reviewReadService.findTop3ByCompanyIdOrderByCreatedAt(
            company.getId());

        if (company.hasImageResource()) {
            String presignedUrl = s3Service.generateGetPresignedUrl(
                company.getImageResource().getObjectKey());
            return CompanyModel.Info.from(company, presignedUrl, services, reviews);
        }
        return CompanyModel.Info.from(company, ImageConstants.DEFAULT_COMPANY_IMAGE_OBJECT_KEY,
            services, reviews);
    }

    @Transactional(readOnly = true)
    public CompanyModel.Summary getCompanySummary(Long id) {
        Company company = companyReadService.findByIdFetchJoinAddressAndImageResource(id);
        List<Review> reviews = reviewReadService.findTop3ByCompanyIdOrderByCreatedAt(
            company.getId());
        Float companyRate = 10F;
        Long rateCount = 100L;
        if (company.hasImageResource()) {
            String presignedUrl = s3Service.generateGetPresignedUrl(
                company.getImageResource().getObjectKey());
            return CompanyModel.Summary.from(company, presignedUrl, companyRate, rateCount,
                reviews);
        }
        return CompanyModel.Summary.from(company, ImageConstants.DEFAULT_COMPANY_IMAGE_OBJECT_KEY,
            companyRate, rateCount, reviews);
    }

    @Transactional
    public void updateCompanyImage(Long companyId, String objectKey) {
        Company company = companyReadService.findByIdFetchJoinImageResource(companyId);
        ImageResource imageResource = imageReadService.findByObjectKey(objectKey);
        if (company.hasImageResource()) {
            applicationEventPublisher.publishEvent(
                S3EventDto.UpdateTag.from(company.getImageResource().getObjectKey(), false));
        }
        company.updateImage(imageResource);
        applicationEventPublisher.publishEvent(S3EventDto.UpdateTag.from(objectKey, true));
    }

    public CompanyModel.Validate validateBusinessNumber(Long userId,
        CompanyCommand.Validate command) {

        CompanyInfraCommand.Validate infraCommand = command.toInfraCommand();
        CompanyInfraModel.Validate infraModel = businessVerificationClient.validate(infraCommand);
        if (!infraModel.isValid()) {
            throw new InvalidBusinessNumberException(ErrorCode.INVALID_BUSINESS_NUMBER);
        }
        CompanySignupVerifyToken verifyToken = companySignupVerifyTokenService.issueToken(
            userId, command.businessNumber());

        return CompanyModel.Validate.from(verifyToken);
    }

}
