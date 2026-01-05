package teamssavice.ssavice.imageresource.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.imageresource.constants.ImageContentType;
import teamssavice.ssavice.imageresource.constants.ImagePath;
import teamssavice.ssavice.imageresource.entity.ImageResource;
import teamssavice.ssavice.imageresource.infrastructure.repository.ImageResourceRepository;

@Service
@RequiredArgsConstructor
public class ImageWriteService {
    private final ImageResourceRepository imageResourceRepository;

    @Transactional
    public ImageResource save(String objectKey, ImagePath path, ImageContentType contentType) {
        ImageResource entity = ImageResource.builder()
                .objectKey(objectKey)
                .path(path)
                .contentType(contentType.mimeType())
                .build();
        return imageResourceRepository.save(entity);
    }
}
