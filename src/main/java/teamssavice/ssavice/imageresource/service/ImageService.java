package teamssavice.ssavice.imageresource.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.imageresource.constants.ImageContentType;
import teamssavice.ssavice.imageresource.constants.ImagePath;
import teamssavice.ssavice.imageresource.service.dto.ImageModel;
import teamssavice.ssavice.s3.S3ObjectKeyGenerator;
import teamssavice.ssavice.s3.S3Service;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final S3Service s3Service;
    private final S3ObjectKeyGenerator s3ObjectKeyGenerator;
    private final ImageWriteService imageWriteService;

    @Transactional
    public ImageModel.Presigned updateProfileImage(Long userId, ImageContentType contentType) {
        String objectKey = s3ObjectKeyGenerator.generator(ImagePath.profile, userId, contentType);
        imageWriteService.save(objectKey, ImagePath.profile, contentType);

        return s3Service.createPresignedUrl(objectKey, contentType);
    }
}
