package ceos.diggindie.common.exception;

import ceos.diggindie.common.response.ApiResponse;
import ceos.diggindie.common.status.ErrorStatus;
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
public class ExceptionAdvice {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(ConstraintViolationException e) {
        // 로그로 상세 에러 기록
        String detailedErrors = e.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining(", "));
        log.warn("ConstraintViolationException - Validation errors: [{}]", detailedErrors);

        return ApiResponse.onFailure(ErrorStatus.VALIDATION_ERROR, "입력값이 올바르지 않습니다.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        String detailedErrors = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.warn("MethodArgumentNotValidException - Field errors: [{}]", detailedErrors);

        return ApiResponse.onFailure(ErrorStatus.VALIDATION_ERROR, "입력값이 올바르지 않습니다.");
    }

    // 리소스를 찾을 수 없음
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoResourceFound(NoResourceFoundException e) {
        log.warn("NoResourceFoundException: {}", e.getMessage());
        return ApiResponse.onFailure(ErrorStatus._NOT_FOUND);
    }

    // 타입 변환 실패 (PathVariable, RequestParam 등)
    @ExceptionHandler({MethodArgumentTypeMismatchException.class, ConversionFailedException.class})
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(Exception e) {
        log.warn("TypeMismatchException: {}", e.getMessage());
        return ApiResponse.onFailure(ErrorStatus._BAD_REQUEST);
    }

    // 커스텀 비즈니스 예외
    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(GeneralException e) {
        if (e.getErrorStatus().getHttpStatus().is5xxServerError()) {
            log.error("GeneralException: {} - {}", e.getErrorStatus().getCode(), e.getMessage());
        } else {
            log.warn("GeneralException: {} - {}", e.getErrorStatus().getCode(), e.getMessage());
        }
        return ApiResponse.onFailure(e.getErrorStatus(), e.getMessage());
    }

    // 그 외 모든 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("Unhandled Exception: ", e);
        return ApiResponse.onFailure(ErrorStatus._INTERNAL_SERVER_ERROR);
    }
}