package teamssavice.ssavice.global.exception;

import teamssavice.ssavice.global.constants.ErrorCode;

public class InvalidBusinessNumberException extends CustomException {

    private static final String DEFAULT_TITLE = "BussinessNumber Validation Error";

    public InvalidBusinessNumberException(ErrorCode errorCode) {
        super(errorCode, DEFAULT_TITLE);
    }

}
