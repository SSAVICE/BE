package teamssavice.ssavice.s3;

import org.springframework.stereotype.Component;
import teamssavice.ssavice.image.constants.ImageContentType;
import teamssavice.ssavice.image.constants.ImagePath;

import java.util.UUID;

@Component
public class S3ObjectKeyGenerator {
   public String generator(ImagePath path, Long userId, ImageContentType contentType) {
      return path.name() + "/" + userId + "/" + UUID.randomUUID() + contentType.extension();
   }
}
