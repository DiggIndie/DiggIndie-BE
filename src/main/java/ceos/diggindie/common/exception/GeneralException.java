package ceos.diggindie.common.exception;


import ceos.diggindie.common.code.ErrorCode;
import lombok.Getter;

@Getter
public class GeneralException extends RuntimeException {

    private final ErrorCode errorCode;

    public GeneralException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public static GeneralException notFound(String message) {
        return new GeneralException(ErrorCode.NOT_FOUND, message);
    }
}