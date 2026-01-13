package ceos.diggindie.common.exception;


import ceos.diggindie.common.code.GeneralErrorCode;
import lombok.Getter;

@Getter
public class GeneralException extends RuntimeException {

    private final GeneralErrorCode errorCode;

    public GeneralException(GeneralErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public static GeneralException notFound(String message) {
        return new GeneralException(GeneralErrorCode.NOT_FOUND, message);
    }
}