package teamssavice.ssavice.imageresource.constants;

import java.util.Arrays;
import teamssavice.ssavice.global.constants.ErrorCode;
import teamssavice.ssavice.global.exception.UnsupportedImageContentTypeException;

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

    // 에러/문서용: 허용 mimeType 목록
    public static String[] allowedMimeTypes() {
        return Arrays.stream(values())
            .map(ImageContentType::mimeType)
            .toArray(String[]::new);
    }

    public static ImageContentType from(String input) {
        String normalized = normalize(input);

        return Arrays.stream(values())
            .filter(it -> it.mimeType.equals(normalized))
            .findFirst()
            .orElseThrow(() -> new UnsupportedImageContentTypeException(
                ErrorCode.UNSUPPORTED_IMAGE_CONTENT_TYPE, input,
                allowedMimeTypes()));
    }

    private static String normalize(String input) {
        return input.split(";")[0].trim().toLowerCase();
    }

    public String mimeType() {
        return mimeType;
    }

    public String extension() {
        return extension;
    }
}