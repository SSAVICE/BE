package teamssavice.ssavice.company.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.company.entity.Company;
import teamssavice.ssavice.company.infrastructure.repository.CompanyRepository;
import teamssavice.ssavice.global.constants.ErrorCode;
import teamssavice.ssavice.global.exception.ConflictException;
import teamssavice.ssavice.global.exception.EntityNotFoundException;
import teamssavice.ssavice.user.entity.Users;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompanyReadService {
    private final CompanyRepository companyRepository;

    @Transactional(readOnly = true)
    public Optional<Company> findByUser(Users users) {
        return companyRepository.findByUser(users);
    }

    @Transactional(readOnly = true)
    public void checkUserExists(Users user) {
        if(companyRepository.existsByUser(user)) {
            throw new ConflictException(ErrorCode.COMPANY_ALREADY_EXISTS);
        }
    }

    @Transactional(readOnly = true)
    public Company findByCompanyIdFetchJoinAddress(Long id) {
        return companyRepository.findByCompanyIdFetchJoinAddress(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.COMPANY_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Company findById(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.COMPANY_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Company findByIdFetchJoinImageResource(Long id) {
        return companyRepository.findByIdFetchJoinImageResource(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.COMPANY_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Company findByIdFetchJoinAddressAndImageResource(Long id) {
        return companyRepository.findByIdFetchJoinAddressAndImageResource(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.COMPANY_NOT_FOUND));
    }
}
