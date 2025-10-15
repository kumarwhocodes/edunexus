package dev.kumar.edunexus.mapper;

import dev.kumar.edunexus.dto.UserDTO;
import dev.kumar.edunexus.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {
    
    UserDTO toDTO(User user);
    
    User toEntity(UserDTO userDTO);
}