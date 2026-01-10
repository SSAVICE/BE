package teamssavice.ssavice.address;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import teamssavice.ssavice.global.constants.ErrorCode;
import teamssavice.ssavice.global.exception.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class RegionReadService {
    private final RegionRepository regionRepository;

    public Region findByRegionCode(String regionCode) {
        return regionRepository.findById(regionCode)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.REGION_NOT_FOUND));
    }
}
