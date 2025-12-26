package teamssavice.ssavice.global.exception;

import teamssavice.ssavice.global.constants.ErrorCode;

public class EntityNotFoundException extends CustomException {
    private static final String DEFAULT_TITLE = "Data Not Found";

    public EntityNotFoundException(ErrorCode errorCode) {
        super(errorCode, DEFAULT_TITLE);
    }
}