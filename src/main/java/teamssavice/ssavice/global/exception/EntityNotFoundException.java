package teamssavice.ssavice.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import teamssavice.ssavice.global.constants.ErrorCode;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EntityNotFoundException extends CustomException {
    private static final String DEFAULT_TITLE = "Data Not Found";

    public EntityNotFoundException(ErrorCode errorCode) {
        super(errorCode, DEFAULT_TITLE);
    }
}