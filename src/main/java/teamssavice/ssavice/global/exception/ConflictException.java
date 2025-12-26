package teamssavice.ssavice.global.exception;

import teamssavice.ssavice.global.constants.ErrorCode;

public class ConflictException extends CustomException {

    private static final String DEFAULT_TITLE = "Conflict";

    public ConflictException(ErrorCode errorCode) {
        super(errorCode, DEFAULT_TITLE);
    }
}
