package teamssavice.ssavice.imageresource.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.global.constants.ErrorCode;
import teamssavice.ssavice.global.exception.EntityNotFoundException;
import teamssavice.ssavice.imageresource.constants.ImageContentType;
import teamssavice.ssavice.imageresource.constants.ImagePath;
import teamssavice.ssavice.imageresource.entity.ImageResource;
import teamssavice.ssavice.imageresource.infrastructure.repository.ImageResourceRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageWriteService {

    private final ImageResourceRepository imageResourceRepository;

    @Transactional
    public ImageResource save(String targetKey, String sourceKey, ImagePath path,
            ImageContentType contentType) {
        ImageResource entity = ImageResource.builder()
                .targetKey(targetKey)
                .sourceKey(sourceKey)
                .path(path)
                .contentType(contentType.mimeType())
                .build();
        return imageResourceRepository.save(entity);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateStatusToProcessing(Long imageResourceId) {
        ImageResource imageResource = imageResourceRepository.findById(imageResourceId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.IMAGE_NOT_FOUND));
        imageResource.startProcessing();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateStatusToDone(Long imageResourceId) {
        ImageResource imageResource = imageResourceRepository.findById(imageResourceId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.IMAGE_NOT_FOUND));
        imageResource.markAsDone();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateStatusToFailed(Long imageResourceId) {
        ImageResource imageResource = imageResourceRepository.findById(imageResourceId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.IMAGE_NOT_FOUND));
        imageResource.markAsFailed();
    }

    @Transactional
    public void changeMetaDataToThumb(String originKey, String thumbKey) {
        imageResourceRepository.findByTargetKey(originKey)
                .ifPresentOrElse(
                        image -> image.confirmAsThumbnail(thumbKey),
                        () -> log.warn("ImageResource not found: {}", originKey)
                );
    }
}
