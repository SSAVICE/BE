package teamssavice.ssavice.image.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import teamssavice.ssavice.image.constants.ImageContentType;
import teamssavice.ssavice.image.constants.ImagePath;
import teamssavice.ssavice.image.entity.ImageResource;
import teamssavice.ssavice.image.infrastructure.repository.ImageResourceRepository;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageResourceRepository imageResourceRepository;

    public void save(String objectKey, ImagePath path, ImageContentType contentType) {
        ImageResource entity = ImageResource.builder()
                .objectKey(objectKey)
                .path(path)
                .contentType(contentType.mimeType())
                .build();
        imageResourceRepository.save(entity);
    }
}
