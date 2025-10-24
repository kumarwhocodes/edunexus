package dev.kumar.edunexus.mapper;

import dev.kumar.edunexus.dto.CourseDTO;
import dev.kumar.edunexus.entity.Course;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CourseMapper {
    
    CourseDTO toDTO(Course course);
    
    Course toEntity(CourseDTO courseDTO);
}