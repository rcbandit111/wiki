package org.engine.mapper;

import org.engine.config.BaseMapperConfig;
import org.engine.production.entity.Users;
import org.engine.rest.dto.UserDTO;
import org.engine.rest.dto.UserFilter;
import org.engine.rest.dto.UserFilterDTO;
import org.engine.rest.dto.UserNewDTO;
import org.mapstruct.Mapper;

@Mapper(config = BaseMapperConfig.class)
public interface UserMapper {

	UserDTO toDTO(Users user);
	
	UserNewDTO toNewDTO(Users user);

	Users map(UserNewDTO userDTO);

	UserFilter toFilter(UserFilterDTO dto);
}
