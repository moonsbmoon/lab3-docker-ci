package edu.platform.catalog;

import edu.platform.lessons.LessonMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {LessonMapper.class})
public interface CourseMapper {
    CourseResponse toResponse(Course course);
}