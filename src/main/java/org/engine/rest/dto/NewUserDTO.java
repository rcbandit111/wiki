package org.engine.rest.dto;

import lombok.Data;

@Data
public class NewUserDTO {

    private String login;

    private String firstName;

    private String lastName;

    private String email;

    private String password;

}
