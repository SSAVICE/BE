package teamssavice.ssavice.global.exception;

import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> methodArgumentNotValidException(
        MethodArgumentNotValidException e
    ) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        Map<String, Object> errors = new HashMap<>();
        e.getAllErrors()
            .forEach(
                field -> errors.put(((FieldError) field).getField(), field.getDefaultMessage()));

        problemDetail.setTitle("Validation Error");
        problemDetail.setProperties(errors);
        return ResponseEntity.badRequest().body(problemDetail);
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> constraintViolationException(
        ConstraintViolationException e
    ) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        Map<String, Object> errors = new HashMap<>();
        e.getConstraintViolations()
            .forEach(
                violation -> errors.put(violation.getPropertyPath().toString(),
                    violation.getMessage()));

        problemDetail.setTitle("Validation Error");
        problemDetail.setProperties(errors);
        return ResponseEntity.badRequest().body(problemDetail);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ProblemDetail> missingServletRequestParameterException(
            MissingServletRequestParameterException e
    ) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Missing Request Parameter");
        problemDetail.setDetail("Required request parameter is missing: " + e.getParameterName());
        return ResponseEntity.badRequest().body(problemDetail);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> httpMessageNotReadableException(
        HttpMessageNotReadableException e
    ) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Invalid Request Body");
        problemDetail.setDetail("요청 바디(JSON) 형식이 올바르지 않습니다.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ProblemDetail> httpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e
    ) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.METHOD_NOT_ALLOWED);
        problemDetail.setTitle("Method Not Allowed");
        problemDetail.setDetail("지원하지 않는 HTTP Method 입니다.");
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(problemDetail);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> unexpectedException(Exception e) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setDetail("Unknown error");
        log.error("Internal Server Error", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ProblemDetail> authenticationException(AuthenticationException e) {
        ProblemDetail problemDetail = setCustomProblemDetail(e);
        return ResponseEntity.status(problemDetail.getStatus()).body(problemDetail);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ProblemDetail> forbiddenException(ForbiddenException e) {
        ProblemDetail problemDetail = setCustomProblemDetail(e);
        return ResponseEntity.status(problemDetail.getStatus()).body(problemDetail);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ProblemDetail> entityNotFoundException(EntityNotFoundException e) {
        ProblemDetail problemDetail = setCustomProblemDetail(e);
        return ResponseEntity.status(problemDetail.getStatus()).body(problemDetail);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ProblemDetail> conflictException(ConflictException e) {
        ProblemDetail problemDetail = setCustomProblemDetail(e);
        return ResponseEntity.status(problemDetail.getStatus()).body(problemDetail);
    }

    private ProblemDetail setCustomProblemDetail(CustomException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(e.getErrorCode().getStatus());
        problemDetail.setTitle(e.getTitle());
        problemDetail.setDetail(e.getMessage());
        problemDetail.setProperty("error_code", e.getErrorCode().getCode());
        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ProblemDetail> methodArgumentTypeMismatchException(
        MethodArgumentTypeMismatchException e
    ) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Invalid Request Parameter");

        // 어떤 파라미터가 문제인지
        String paramName = e.getName();
        problemDetail.setDetail(paramName + " 파라미터 값이 올바르지 않습니다.");

        // status 같은 enum이면 허용 값도 함께 내려주기
        e.getRequiredType();
        if (e.getRequiredType().isEnum()) {
            Object[] enumConstants = e.getRequiredType().getEnumConstants();
            problemDetail.setProperty("allowed_values", enumConstants);
        }

        return ResponseEntity.badRequest().body(problemDetail);
    }


    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<ProblemDetail> externalApiException(ExternalApiException e) {
        ProblemDetail problemDetail = setCustomProblemDetail(e);
        problemDetail.setDetail(e.getMessage());

        return ResponseEntity.status(problemDetail.getStatus()).body(problemDetail);
    }




}
