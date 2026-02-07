package teamssavice.ssavice.imageresource.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teamssavice.ssavice.imageresource.constants.ImageContentType;
import teamssavice.ssavice.imageresource.constants.ImagePath;
import teamssavice.ssavice.imageresource.constants.ImageVariant;
import teamssavice.ssavice.imageresource.entity.ImageResource;
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
    private final ImageReadService imageReadService;

    @Transactional
    public ImageModel.PutPresignedUrl updateImage(Long id, ImagePath path, ImageContentType contentType) {
        String tempKey = s3ObjectKeyGenerator.tempGenerator(path, id, contentType);
        String objectKey = s3ObjectKeyGenerator.originGenerator(path, ImageVariant.origin, id,
                contentType);
        imageWriteService.save(objectKey, tempKey, path, contentType);

        return s3Service.createPutPresignedUrl(tempKey, contentType);
    }

    @Transactional
    public List<ImageModel.PutPresignedUrl> updateImages(ImageCommand.PutPresignedUrls command) {
        List<ImageModel.PutPresignedUrl> models = new ArrayList<>();
        for (ImageContentType contentType : command.add()) {
            models.add(updateImage(command.companyId(), command.path(), contentType));
        }
        return models;
    }

    @Transactional
    public void deActivateImages(List<Long> imageIds) {

        if (imageIds == null || imageIds.isEmpty()) {
            return;
        }

        List<ImageResource> images = imageReadService.findAllById(imageIds);

        images.forEach(ImageResource::deActivate);
    }

    public void handleImageMove(Long imageResourceId, String sourceKey, String targetKey, ImageContentType contentType) {
        imageWriteService.updateStatusToProcessing(imageResourceId);
        try {
            s3Service.copyObject(sourceKey, targetKey, contentType);
            imageWriteService.updateStatusToDone(imageResourceId);
        } catch (Exception e) {
            imageWriteService.updateStatusToFailed(imageResourceId);
            throw e;
        }
    }
}
