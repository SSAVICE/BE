package teamssavice.ssavice.imageresource.service;

import java.util.List;
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

    private final ImageResourceRepository imageResourceRepository;

    @Transactional(readOnly = true)
    public ImageResource findBySourceKey(String objectKey) {
        return imageResourceRepository.findBySourceKey(objectKey)
            .orElseThrow(() -> new EntityNotFoundException(ErrorCode.IMAGE_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<ImageResource> findAllBySourceKeyIn(List<String> objectKeys) {
        return imageResourceRepository.findAllBySourceKeyIn(objectKeys);
    }

    @Transactional(readOnly = true)
    public List<ImageResource> findAllById(List<Long> ids) {
        return imageResourceRepository.findAllById(ids);
    }

    @Transactional(readOnly = true)
    public ImageResource findByTargetKey(String objectKey) {
        return imageResourceRepository.findByTargetKey(objectKey)
            .orElseThrow(() -> new EntityNotFoundException(ErrorCode.IMAGE_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public ImageResource findById(Long id) {
        return imageResourceRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(ErrorCode.IMAGE_NOT_FOUND));
    }
}
