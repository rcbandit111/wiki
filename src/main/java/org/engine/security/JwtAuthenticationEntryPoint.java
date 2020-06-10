package org.engine.security;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.engine.exception.ErrorDetail;
import org.engine.exception.ErrorResponse;
import org.engine.exception.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException ex) throws IOException {

        ErrorDetail errorDetail = ErrorDetail.NOT_FOUND;

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

        response.setStatus(errorDetail.getHttpStatus().value());
        String json = new ObjectMapper().setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL).writeValueAsString(errorResponse);
        response.getWriter().write(json);
        response.flushBuffer();
    }
}
