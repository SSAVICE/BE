package teamssavice.ssavice.global.exception;

import teamssavice.ssavice.global.constants.ErrorCode;

public class UnsupportedImageContentTypeException extends CustomException {

    private static final String DEFAULT_TITLE = "Validation Error";
    private final String invalidValue;
    private final String[] allowedValues;

    public UnsupportedImageContentTypeException(
        ErrorCode errorCode,
        String invalidValue,
        String[] allowedValues
    ) {
        super(errorCode, DEFAULT_TITLE);
        this.invalidValue = invalidValue;
        this.allowedValues = allowedValues;
    }

    public String getInvalidValue() {
        return invalidValue;
    }

    public String[] getAllowedValues() {
        return allowedValues;
    }
}
