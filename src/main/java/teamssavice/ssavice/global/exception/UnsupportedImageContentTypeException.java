package teamssavice.ssavice.global.exception;

import teamssavice.ssavice.global.constants.ErrorCode;

public class UnsupportedImageContentTypeException extends CustomException {

    private static final String DEFAULT_TITLE = "Validation Error";

    public UnsupportedImageContentTypeException(ErrorCode errorCode) {
        super(errorCode, DEFAULT_TITLE);
    }
}
