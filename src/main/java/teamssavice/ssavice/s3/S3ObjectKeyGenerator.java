package teamssavice.ssavice.s3;

import java.util.UUID;
import org.springframework.stereotype.Component;
import teamssavice.ssavice.imageresource.constants.ImageContentType;
import teamssavice.ssavice.imageresource.constants.ImagePath;
import teamssavice.ssavice.imageresource.constants.ImageVariant;

@Component
public class S3ObjectKeyGenerator {

    public String tempGenerator(ImagePath path, Long id, ImageContentType contentType) {
        return ImagePath.temp + "/" + path.name() + "/" + id + "/" + UUID.randomUUID()
            + contentType.extension();
    }

    public String originGenerator(ImagePath path, ImageVariant variant, Long userId,
        ImageContentType contentType) {
        return path.name() + "/" + variant.name() + "/" + userId + "/" + UUID.randomUUID()
            + contentType.extension();
    }
}
