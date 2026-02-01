package teamssavice.ssavice.imageresource.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import teamssavice.ssavice.imageresource.constants.ImageContentType;
import teamssavice.ssavice.imageresource.entity.ImageResource;
import teamssavice.ssavice.s3.S3Service;

@Service
@RequiredArgsConstructor
public class ImageAsyncService {

    private final S3Service s3Service;
    private final ImageReadService imageReadService;
    private final ImageWriteService imageWriteService;

    @Async("imageExecutor")
    public void moveAsync(String sourceKey) {
        ImageResource image = imageReadService.findBySourceKey(sourceKey);

        if (!imageWriteService.acquireProcessing(image.getId())) {
            return;
        }

        try {
            s3Service.moveObject(
                image.getSourceKey(),
                image.getTargetKey(),
                ImageContentType.from(image.getContentType())
            );

            imageWriteService.markDoneIfProcessing(image.getId());

        } catch (Exception e) {
            imageWriteService.markFailedIfProcessing(image.getId());
        }
    }
}
