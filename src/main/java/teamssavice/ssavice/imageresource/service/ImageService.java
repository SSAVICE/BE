package teamssavice.ssavice.imageresource.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.imageresource.constants.ImageContentType;
import teamssavice.ssavice.imageresource.constants.ImagePath;
import teamssavice.ssavice.imageresource.service.dto.ImageCommand;
import teamssavice.ssavice.imageresource.service.dto.ImageModel;
import teamssavice.ssavice.s3.S3ObjectKeyGenerator;
import teamssavice.ssavice.s3.S3Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final S3Service s3Service;
    private final S3ObjectKeyGenerator s3ObjectKeyGenerator;
    private final ImageWriteService imageWriteService;

    @Transactional
    public ImageModel.PutPresignedUrl updateImage(Long id, ImagePath path, ImageContentType contentType) {
        String objectKey = s3ObjectKeyGenerator.generator(path, id, contentType);
        imageWriteService.save(objectKey, path, contentType);

        return s3Service.createPresignedUrl(objectKey, contentType);
    }

    @Transactional
    public List<ImageModel.PutPresignedUrl> updateImages(ImageCommand.PutPresignedUrls command) {
        List<ImageModel.PutPresignedUrl> models = new ArrayList<>();
        for (ImageContentType contentType : command.add()) {
            models.add(updateImage(command.companyId(), command.path(), contentType));
        }
        return models;
    }
}
