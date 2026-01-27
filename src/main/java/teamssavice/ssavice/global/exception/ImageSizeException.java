package teamssavice.ssavice.global.exception;

import lombok.Getter;
import teamssavice.ssavice.global.constants.ErrorCode;

@Getter
public class ImageSizeException extends CustomException {

    private static final String DEFAULT_TITLE = "Image Size Exceeded";
    private final long currentSize;
    private final long maxAllowedSize;

    public ImageSizeException(ErrorCode errorCode, long currentSize, long maxAllowedSize) {
        super(errorCode, DEFAULT_TITLE);
        this.currentSize = currentSize;
        this.maxAllowedSize = maxAllowedSize;
    }
}
