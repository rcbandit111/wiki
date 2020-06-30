package org.engine.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Used in JWT token successful response
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AuthenticationTokenDTO {

    // Server Response data

    private String access_token;

    private String token_type;

    private Long expires_in;
}
