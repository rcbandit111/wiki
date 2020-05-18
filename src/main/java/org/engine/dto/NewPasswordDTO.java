package org.engine.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class NewPasswordDTO {

    // Request data

    @NotNull
    @NotEmpty
    @Size(min=8, max=15, message = "'Password' value '${validatedValue}' must be between {min} and {max} characters long.")
    private String password;

    @NotNull
    @NotEmpty
    @Size(min=8, max=15, message = "'Confirmation Password' value '${validatedValue}' must be between {min} and {max} characters long.")
    private String confirmPassword;

    // Server Response data

    private Integer status;

    private String error;

    private String errorDescription;
}
