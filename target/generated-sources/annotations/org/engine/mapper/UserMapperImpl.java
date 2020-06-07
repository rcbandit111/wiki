package org.engine.mapper;

import java.time.format.DateTimeFormatter;
import javax.annotation.processing.Generated;
import org.engine.production.entity.Users;
import org.engine.rest.dto.UserDTO;
import org.engine.rest.dto.UserDTO.UserDTOBuilder;
import org.engine.rest.dto.UserFilter;
import org.engine.rest.dto.UserFilterDTO;
import org.engine.rest.dto.UserNewDTO;
import org.engine.rest.dto.UserNewDTO.UserNewDTOBuilder;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2020-06-05T09:52:42+0400",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 11.0.7 (Ubuntu)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDTO toDTO(Users user) {
        if ( user == null ) {
            return null;
        }

        UserDTOBuilder userDTO = UserDTO.builder();

        userDTO.id( user.getId() );
        userDTO.login( user.getLogin() );
        userDTO.firstName( user.getFirstName() );
        userDTO.lastName( user.getLastName() );
        userDTO.email( user.getEmail() );
        userDTO.role( user.getRole() );
        userDTO.enabled( user.getEnabled() );
        if ( user.getCreatedAt() != null ) {
            userDTO.createdAt( DateTimeFormatter.ISO_LOCAL_DATE_TIME.format( user.getCreatedAt() ) );
        }

        return userDTO.build();
    }

    @Override
    public UserNewDTO toNewDTO(Users user) {
        if ( user == null ) {
            return null;
        }

        UserNewDTOBuilder userNewDTO = UserNewDTO.builder();

        userNewDTO.id( user.getId() );
        userNewDTO.login( user.getLogin() );
        userNewDTO.firstName( user.getFirstName() );
        userNewDTO.lastName( user.getLastName() );
        userNewDTO.email( user.getEmail() );
        userNewDTO.role( user.getRole() );
        userNewDTO.enabled( user.getEnabled() );
        userNewDTO.ownerId( user.getOwnerId() );
        userNewDTO.ownerType( user.getOwnerType() );
        userNewDTO.createdAt( user.getCreatedAt() );

        return userNewDTO.build();
    }

    @Override
    public Users map(UserNewDTO userDTO) {
        if ( userDTO == null ) {
            return null;
        }

        Users users = new Users();

        if ( userDTO.getId() != null ) {
            users.setId( userDTO.getId() );
        }
        users.setLogin( userDTO.getLogin() );
        users.setEmail( userDTO.getEmail() );
        users.setEnabled( userDTO.getEnabled() );
        users.setOwnerId( userDTO.getOwnerId() );
        users.setOwnerType( userDTO.getOwnerType() );
        users.setRole( userDTO.getRole() );
        users.setFirstName( userDTO.getFirstName() );
        users.setLastName( userDTO.getLastName() );
        users.setCreatedAt( userDTO.getCreatedAt() );

        return users;
    }

    @Override
    public UserFilter toFilter(UserFilterDTO dto) {
        if ( dto == null ) {
            return null;
        }

        UserFilter userFilter = new UserFilter();

        userFilter.setId( dto.getId() );
        userFilter.setName( dto.getName() );
        userFilter.setFrom( dto.getFrom() );
        userFilter.setTo( dto.getTo() );

        return userFilter;
    }
}
