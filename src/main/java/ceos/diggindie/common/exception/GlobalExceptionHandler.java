package ceos.diggindie.common.exception;

import ceos.diggindie.common.code.ErrorCode;
import ceos.diggindie.common.response.Response;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Response<Void>> handleConstraintViolation(ConstraintViolationException e) {

        ErrorCode errorCode = ErrorCode.VALIDATION_ERROR;
        String detailedErrors = e.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining(", "));
        log.warn("ConstraintViolationException - Validation errors: [{}]", detailedErrors);

        return ResponseEntity.status(errorCode.getStatusCode()).body(Response.fail(errorCode));
    }

    // @RequestBody 검증 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response<Void>> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {

        ErrorCode errorCode = ErrorCode.VALIDATION_ERROR;
        String detailedErrors = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.warn("MethodArgumentNotValidException - Field errors: [{}]", detailedErrors);

        return ResponseEntity.status(errorCode.getStatusCode()).body(Response.fail(errorCode));
    }

    // 리소스를 찾을 수 없음
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Response<Void>> handleNoResourceFound(NoResourceFoundException e) {
        ErrorCode errorCode = ErrorCode.NOT_FOUND;
        log.warn("NoResourceFoundException: {}", e.getMessage());

        return ResponseEntity.status(errorCode.getStatusCode()).body(Response.fail(errorCode));
    }

    // 타입 변환 실패 (PathVariable, RequestParam 등)
    @ExceptionHandler({MethodArgumentTypeMismatchException.class, ConversionFailedException.class})
    public ResponseEntity<Response<Void>> handleTypeMismatch(Exception e) {
        ErrorCode errorCode = ErrorCode.BAD_REQUEST;
        log.warn("TypeMismatchException: {}", e.getMessage());

        return ResponseEntity.status(errorCode.getStatusCode()).body(Response.fail(errorCode));
    }

    // 커스텀 비즈니스 예외
    // 추후 에러 핸들링 세팅에서 수정해주시길 바랍니다!
    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<Response<Void>> handleGeneralException(GeneralException e) {
        return null;
    }

    // 그 외 모든 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<Void>> handleException(Exception e) {

        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        log.error("Unhandled Exception: ", e);

        return ResponseEntity.status(errorCode.getStatusCode()).body(Response.fail(errorCode));
    }
}