package teamssavice.ssavice.global.constants;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // Auth
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "EXPIRED_TOKEN", "만료된 토큰입니다."),
    UNSUPPORTED_TOKEN(HttpStatus.UNAUTHORIZED, "UNSUPPORTED_TOKEN", "지원되지 않는 토큰입니다."),
    MISSING_TOKEN(HttpStatus.UNAUTHORIZED, "MISSING_TOKEN", "토큰이 존재하지 않습니다."),
    UNKNOWN_TOKEN_ERROR(HttpStatus.UNAUTHORIZED, "UNKNOWN_TOKEN_ERROR", "알 수 없는 토큰 에러"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "FORBIDDEN", "접근 권한이 없습니다."),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "사용자를 찾을 수 없습니다."),
    USER_EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "USER_EMAIL_ALREADY_EXISTS", "이미 사용 중인 이메일입니다."),

    // Company
    COMPANY_ALREADY_EXISTS(HttpStatus.CONFLICT, "COMPANY_ALREADY_EXISTS", "이미 업체가 등록되어 있습니다."),
    COMPANY_NOT_FOUND(HttpStatus.NOT_FOUND, "COMPANY_NOT_FOUND", "업체 정보를 찾을 수 없습니다."),

    // serviceItem
    SERVICE_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "SERVICE_NOT_FOUND", "해당하는 서비스를 찾을 수 없습니다."),

    // imageResource
    UNSUPPORTED_IMAGE_CONTENT_TYPE(HttpStatus.BAD_REQUEST, "UNSUPPORTED_IMAGE_CONTENT_TYPE", "지원하지 않는 contentType입니다."),
    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "IMAGE_NOT_FOUND", "이미지를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
