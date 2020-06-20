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
public class ResetPasswordTokenDTO {

    // Request data

    @NotNull
    @NotEmpty
    @Size(min=200, max=255, message = "'Reset Password Token' value '${validatedValue}' must be between {min} and {max} characters long.")
    private String resetPasswordToken;

    // Server Response data

    private Integer status;

    private String error;

    private String errorDescription;
}
