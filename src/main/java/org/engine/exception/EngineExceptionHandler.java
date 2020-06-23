package org.engine.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class EngineExceptionHandler {

	private static final Logger LOG = LoggerFactory.getLogger(EngineExceptionHandler.class);

	@ExceptionHandler({ AccessDeniedException.class })
	public ResponseEntity<ErrorResponseDTO> accessDeniedExceptionHandler(final AccessDeniedException ex) {
		ErrorDetail errorDetail = ErrorDetail.NOT_FOUND;

		LOG.error(ex.getMessage(), ex.getCause());
		ErrorResponse errorEntry = new ErrorResponse();
		errorEntry.setTitle(errorDetail.getTitle());
		errorEntry.setCode(errorDetail.getErrorCode());
		HttpStatus httpStatus = ErrorDetail.getHttpStatusBasedOnErrorCode(errorDetail.getErrorCode());
		errorEntry.setStatus(httpStatus.value());
		errorEntry.setDetail(ex.getMessage());
		Map<String, String> extra = new HashMap<String, String>();
		extra.put("detail", ex.getMessage());
		errorEntry.setExtra(extra);

		ErrorResponseDTO errorResponse = new ErrorResponseDTO();
		errorResponse.setErrors(Arrays.asList(errorEntry));

		return new ResponseEntity<ErrorResponseDTO>(errorResponse, HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler({ Exception.class})
	public ResponseEntity<ErrorResponseDTO> serverExceptionHandler(final Exception ex) {
		ErrorDetail errorDetail = ErrorDetail.NOT_FOUND;

		LOG.error(ex.getMessage(), ex.getCause());
		ErrorResponse errorEntry = new ErrorResponse();
		errorEntry.setTitle(errorDetail.getTitle());
		errorEntry.setCode(errorDetail.getErrorCode());
		HttpStatus httpStatus = ErrorDetail.getHttpStatusBasedOnErrorCode(errorDetail.getErrorCode());
		errorEntry.setStatus(httpStatus.value());
		errorEntry.setDetail(ex.getMessage());
		Map<String, String> extra = new HashMap<String, String>();
		extra.put("detail", ex.getMessage());
		errorEntry.setExtra(extra);

		ErrorResponseDTO errorResponse = new ErrorResponseDTO();
		errorResponse.setErrors(Arrays.asList(errorEntry));

		return new ResponseEntity<ErrorResponseDTO>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(BaseException.class)
	public ResponseEntity<ErrorResponseDTO> handleException(BaseException ex) {
		LOG.error(ex.getMessage(), ex.getCause());
		ErrorResponse errorEntry = new ErrorResponse();
		errorEntry.setTitle(ex.getTitle());
		errorEntry.setCode(ex.getErrorCode());
		HttpStatus httpStatus = ErrorDetail.getHttpStatusBasedOnErrorCode(ex.getErrorCode());
		errorEntry.setStatus(httpStatus.value());
		errorEntry.setDetail(ex.getMessage());
		errorEntry.setExtra(ex.getExtra());

		ErrorResponseDTO errorResponse = new ErrorResponseDTO();
		errorResponse.setErrors(Arrays.asList(errorEntry));

		return new ResponseEntity<ErrorResponseDTO>(errorResponse, httpStatus);
	}
}
