package teamssavice.ssavice.global.exception;


import teamssavice.ssavice.global.constants.ErrorCode;

public class ExternalApiException extends CustomException {

    private static final String DEFAULT_TITLE = "External API Error";

    public ExternalApiException(ErrorCode errorCode) {
        super(errorCode, DEFAULT_TITLE);
    }
}
