package teamssavice.ssavice.image.constants;

import teamssavice.ssavice.global.constants.ErrorCode;
import teamssavice.ssavice.global.exception.UnsupportedImageContentTypeException;

import java.util.Arrays;

public enum ImageContentType {

    JPEG("image/jpeg", ".jpg"),
    PNG("image/png", ".png"),
    WEBP("image/webp", ".webp");

    private final String mimeType;
    private final String extension;

    ImageContentType(String mimeType, String extension) {
        this.mimeType = mimeType;
        this.extension = extension;
    }

    public String mimeType() {
        return mimeType;
    }

    public String extension() {
        return extension;
    }

    public static ImageContentType from(String input) {
        String normalized = normalize(input);

        return Arrays.stream(values())
                .filter(it -> it.mimeType.equals(normalized))
                .findFirst()
                .orElseThrow(() -> new UnsupportedImageContentTypeException(ErrorCode.UNSUPPORTED_IMAGE_CONTENT_TYPE));
    }

    private static String normalize(String input) {
        return input.split(";")[0].trim().toLowerCase();
    }
}