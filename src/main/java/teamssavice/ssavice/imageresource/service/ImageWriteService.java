package teamssavice.ssavice.imageresource.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.imageresource.constants.ImageContentType;
import teamssavice.ssavice.imageresource.constants.ImagePath;
import teamssavice.ssavice.imageresource.constants.ImageStatus;
import teamssavice.ssavice.imageresource.entity.ImageResource;
import teamssavice.ssavice.imageresource.infrastructure.repository.ImageResourceRepository;

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

    @Transactional
    public boolean acquireProcessing(Long imageId) {
        return imageResourceRepository.updateStatusIfIn(
            imageId,
            ImageStatus.PROCESSING,
            List.of(ImageStatus.PENDING, ImageStatus.FAILED)
        ) == 1;
    }

    @Transactional
    public void markDoneIfProcessing(Long imageId) {
        imageResourceRepository.updateStatusAndActiveWhen(
            imageId,
            ImageStatus.DONE,
            true,
            ImageStatus.PROCESSING
        );
    }

    @Transactional
    public void markFailedIfProcessing(Long imageId) {
        imageResourceRepository.updateStatusAndActiveWhen(
            imageId,
            ImageStatus.FAILED,
            false,
            ImageStatus.PROCESSING
        );
    }
}
