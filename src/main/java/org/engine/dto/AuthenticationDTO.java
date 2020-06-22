package org.engine.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Used in login page in Angular
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AuthenticationDTO {

    // Request data

    @NotNull
    @NotEmpty
    @Size(min=3, max=10, message = "'Name' value '${validatedValue}' must be between {min} and {max} characters long.")
    private String name;

    @NotNull
    @NotEmpty
    @Size(min=3, max=15, message = "'Password' value '${validatedValue}' must be between {min} and {max} characters long.")
    private String password;

//    @NotNull
//    @NotEmpty
//    @Size(min=2, max=15, message = "'Email' value '${validatedValue}' must be between {min} and {max} characters long.")
//    private String email;

    // Server Response data

    private Integer status;

    private String error;

    private String errorDescription;

}
