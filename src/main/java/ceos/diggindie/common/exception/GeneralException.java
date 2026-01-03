package ceos.diggindie.common.exception;


import ceos.diggindie.common.status.ErrorStatus;
import lombok.Getter;

@Getter
public class GeneralException extends RuntimeException {

    private final ErrorStatus errorStatus;

    public GeneralException(ErrorStatus errorStatus) {
        super(errorStatus.getMessage());
        this.errorStatus = errorStatus;
    }

    public GeneralException(ErrorStatus errorStatus, String message) {
        super(message);
        this.errorStatus = errorStatus;
    }

    public GeneralException(ErrorStatus errorStatus, Throwable cause) {
        super(errorStatus.getMessage(), cause);
        this.errorStatus = errorStatus;
    }

    public GeneralException(String message) {
        super(message);
        this.errorStatus = ErrorStatus._INTERNAL_SERVER_ERROR;
    }

    public GeneralException(String message, Throwable cause) {
        super(message, cause);
        this.errorStatus = ErrorStatus._INTERNAL_SERVER_ERROR;
    }

    // 자주 사용하는 예외 팩토리 메서드
    public static GeneralException loginRequired() {
        return new GeneralException(ErrorStatus.LOGIN_REQUIRED);
    }

    public static GeneralException notFound(String message) {
        return new GeneralException(ErrorStatus._NOT_FOUND, message);
    }

    public static GeneralException badRequest(String message) {
        return new GeneralException(ErrorStatus._BAD_REQUEST, message);
    }
}