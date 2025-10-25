package dev.kumar.edunexus.mapper;

import dev.kumar.edunexus.dto.UserDTO;
import dev.kumar.edunexus.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    
    @Mapping(target = "enrolledCourses", ignore = true)
    UserDTO toDTO(User user);
    
    User toEntity(UserDTO userDTO);
}