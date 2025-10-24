package dev.kumar.edunexus.controller;

import dev.kumar.edunexus.dto.CourseDTO;
import dev.kumar.edunexus.dto.CustomResponse;
import dev.kumar.edunexus.dto.progress.CourseProgressDTO;
import dev.kumar.edunexus.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/course")
@RequiredArgsConstructor
public class CourseController {
    
    private final CourseService service;
    
    @GetMapping("/all")
    public CustomResponse<List<CourseDTO>> getAllCoursesHandler() {
        return new CustomResponse<>(
                HttpStatus.OK,
                "Courses fetched successfully",
                service.getAllCourses()
        );
    }
    
    @GetMapping("/{courseId}/progress")
    public CustomResponse<CourseProgressDTO> getCourseProgressHandler(
            @PathVariable UUID courseId,
            @RequestHeader("Authorization") String token
    ) {
        return new CustomResponse<>(
                HttpStatus.OK,
                "Course progress fetched successfully",
                service.getCourseProgress(courseId, token)
        );
    }
    
    @PostMapping("/{courseId}/enroll")
    public CustomResponse<Void> enrollCourseHandler(
            @PathVariable UUID courseId,
            @RequestHeader("Authorization") String token
    ) {
        service.enrollCourse(courseId, token);
        return new CustomResponse<>(
                HttpStatus.OK,
                "Enrolled in course successfully",
                null
        );
    }
    
    @PostMapping
    public CustomResponse<CourseDTO> createCourseHandler(@RequestBody CourseDTO courseDTO) {
        return new CustomResponse<>(
                HttpStatus.CREATED,
                "Course created successfully",
                service.createCourse(courseDTO)
        );
    }
    
    @PutMapping("/{courseId}")
    public CustomResponse<CourseDTO> updateCourseHandler(
            @PathVariable UUID courseId,
            @RequestBody CourseDTO courseDTO
    ) {
        return new CustomResponse<>(
                HttpStatus.OK,
                "Course updated successfully",
                service.updateCourse(courseId, courseDTO)
        );
    }
    
    @DeleteMapping("/{courseId}")
    public CustomResponse<Void> deleteCourseHandler(@PathVariable UUID courseId) {
        service.deleteCourse(courseId);
        return new CustomResponse<>(
                HttpStatus.OK,
                "Course deleted successfully",
                null
        );
    }
}