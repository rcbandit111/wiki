package org.engine.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorDetail implements ErrorInfo {

	NOT_FOUND("1000", "Not found", "Not found", "Not found", HttpStatus.NOT_FOUND),
	BAD_CREDENTIALS("1000", "Bad Credentials", "Bad Credentials", "Bad Credentials", HttpStatus.NOT_FOUND),
	EXPIRED_CREDENTIALS("1000", "Expired Credentials", "Expired Credentials", "Expired Credentials", HttpStatus.NOT_FOUND),
	DISABLED_USER("1000", "Disabled user", "Disabled user", "Disabled user", HttpStatus.NOT_FOUND),
	AUTHENTICATION_ERROR("1000", "Authentication error", "Authentication error", "Authentication error", HttpStatus.NOT_FOUND),
	TEMPLATE_NOT_FOUND("1000", "Template not found", "Internal Error", "Internal Error", HttpStatus.NOT_FOUND);

	private String errorCode;
	private String message;
	private String detail;
	private String title;
	private HttpStatus httpStatus;

	private ErrorDetail(final String errorCode, final String message, final String detail, final String title, final HttpStatus httpStatus)
	{
		this.errorCode = errorCode;
		this.message = message;
		this.detail = detail;
		this.title = title;
		this.httpStatus = httpStatus;
	}

	public static HttpStatus getHttpStatusBasedOnErrorCode(String errorCode) {
		for(ErrorDetail e : ErrorDetail.values()) {
			if(errorCode.equals(e.getErrorCode())) {
				return e.getHttpStatus();
			}
		}
		return HttpStatus.INTERNAL_SERVER_ERROR;
	}
}
