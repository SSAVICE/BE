package teamssavice.ssavice.fixture;

import teamssavice.ssavice.imageresource.constants.ImagePath;
import teamssavice.ssavice.imageresource.entity.ImageResource;

public class ImageResourceFixture {

    public static ImageResource imageResource() {
        return ImageResource.builder()
                .objectKey("objectKey")
                .path(ImagePath.profile)
                .contentType("image/jpeg")
                .build();
    }
}
