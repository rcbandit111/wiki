package org.engine.rest.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserNewDTO {

	private Integer id;
   
    private String login;
        
    private String firstName;
    
    private String lastName;
    
    private String email;
    
    private String role;

    private Boolean enabled;	
    
    private Integer ownerId;
    
    private String ownerType;	
    
    private LocalDateTime lastActivityAt;	
    
    private LocalDateTime createdAt;
}
