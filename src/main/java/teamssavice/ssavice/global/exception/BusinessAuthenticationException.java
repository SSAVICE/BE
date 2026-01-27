package teamssavice.ssavice.global.exception;

import teamssavice.ssavice.global.constants.ErrorCode;

public class BusinessAuthenticationException extends CustomException {

    private static final String DEFAULT_TITLE = "Business Authentication Error";

    public BusinessAuthenticationException(ErrorCode errorCode) {
        super(errorCode, DEFAULT_TITLE);
    }

}
