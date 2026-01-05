package teamssavice.ssavice.imageresource.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.global.constants.ErrorCode;
import teamssavice.ssavice.global.exception.EntityNotFoundException;
import teamssavice.ssavice.imageresource.entity.ImageResource;
import teamssavice.ssavice.imageresource.infrastructure.repository.ImageResourceRepository;

@Service
@RequiredArgsConstructor
public class ImageReadService {
    private final ImageResourceRepository repository;

    @Transactional(readOnly = true)
    public ImageResource findByObjectKey(String objectKey) {
        return repository.findByObjectKey(objectKey)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.IMAGE_NOT_FOUND));
    }
}
