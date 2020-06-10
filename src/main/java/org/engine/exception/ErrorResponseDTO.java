package org.engine.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ErrorResponseDTO {
    List<ErrorResponse> errors = new ArrayList<>();
    public ErrorResponseDTO(List<ErrorResponse> errorResponses){
        this.errors = errorResponses;
    }
}
