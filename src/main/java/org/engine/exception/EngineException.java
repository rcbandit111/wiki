package org.engine.exception;

import org.springframework.http.HttpStatus;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class EngineException extends BaseException {

    private static final long serialVersionUID = -574200287084497647L;

    private String detail;
    private String message;
    private HttpStatus httpStatus;
    private int httpStatusCode;

    public EngineException(final ErrorDetail error) {
        super(error, error.getDetail());
        this.detail = error.getDetail();
        this.message = error.getMessage();
        this.httpStatus = error.getHttpStatus();
        this.httpStatusCode = error.getHttpStatus().value();
    }

    public EngineException(final ErrorDetail error, final Object... args) {
        super(error, MessageFormat.format(error.getMessage(), args));
        this.detail = error.getDetail();
        this.message = error.getMessage();
        this.httpStatus = error.getHttpStatus();
        this.httpStatusCode = error.getHttpStatus().value();
    }

    public EngineException(final ErrorDetail error, final String detail) {
        super(error, detail);
        this.detail = detail;
        this.message = error.getMessage();
        this.httpStatus = error.getHttpStatus();
        this.httpStatusCode = error.getHttpStatus().value();
    }

    public EngineException(final ErrorDetail error, final String detail, final String message) {
        super(error, detail);
        this.detail = detail;
        this.message = message;
        this.httpStatus = error.getHttpStatus();
        this.httpStatusCode = error.getHttpStatus().value();
    }

    @Override
    public Map<String, String> getExtra() {
        Map<String, String> extra = new HashMap<>();
        extra.put("detail", detail);
        extra.put("message", message);
        extra.put("httpStatusCode", String.valueOf(httpStatusCode));
        return extra;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
