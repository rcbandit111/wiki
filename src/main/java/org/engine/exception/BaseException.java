package org.engine.exception;


import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
public abstract class BaseException extends RuntimeException {

    private final String errorCode;
    private final String title;

    @Setter
    private String sourcePointer;
    @Setter
    private String sourceParameter;

    public BaseException(ErrorInfo errorInfo) {
        super();
        errorCode = errorInfo.getErrorCode();
        title = errorInfo.getTitle();
    }

    public BaseException(ErrorInfo errorInfo, String message) {
        super(message);
        errorCode = errorInfo.getErrorCode();
        title = errorInfo.getTitle();
    }

    public BaseException(ErrorInfo errorInfo, String message, Throwable cause) {
        super(message, cause);
        errorCode = errorInfo.getErrorCode();
        title = errorInfo.getTitle();
    }

    public BaseException(ErrorInfo errorInfo, Throwable cause) {
        super(cause);
        errorCode = errorInfo.getErrorCode();
        title = errorInfo.getTitle();
    }

    public abstract Map<String, String> getExtra();

}
