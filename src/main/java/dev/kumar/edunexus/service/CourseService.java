package dev.kumar.edunexus.service;

import dev.kumar.edunexus.dto.*;
import dev.kumar.edunexus.dto.progress.*;
import dev.kumar.edunexus.entity.*;
import dev.kumar.edunexus.exception.ResourceNotFoundException;
import dev.kumar.edunexus.mapper.CourseMapper;
import dev.kumar.edunexus.repository.*;
import dev.kumar.edunexus.util.FirebaseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {
    
    private final CourseRepository courseRepo;
    private final SectionRepository sectionRepo;
    private final UnitRepository unitRepo;
    private final LevelRepository levelRepo;
    private final UserCourseRepository userCourseRepo;
    private final UserLevelProgressRepository progressRepo;
    private final UserRepository userRepo;
    private final CourseMapper courseMapper;
    private final FirebaseUtil firebaseUtil;
    
    public List<CourseDTO> getAllCourses() {
        try {
            return courseRepo.findAll().stream()
                    .map(courseMapper::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println("Error fetching courses: " + e.getMessage());
            throw new RuntimeException("Failed to fetch courses");
        }
    }
    
    public CourseProgressDTO getCourseProgress(UUID courseId, String token) {
        String userId = extractUserId(token);
        
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        
        if(!userCourseRepo.existsByUserIdAndCourseId(userId, courseId)){
            throw new ResourceNotFoundException("User not enrolled in course");
        }
        
        List<Section> sections = sectionRepo.findByCourseId(courseId);
        Map<UUID, Boolean> levelCompletionMap = getLevelCompletionMap(userId, courseId);
        
        List<SectionProgressDTO> sectionProgress = sections.stream()
                .map(section -> {
                    List<Unit> units = unitRepo.findBySectionId(section.getId());
                    List<UnitProgressDTO> unitProgress = units.stream()
                            .map(unit -> {
                                List<Level> levels = levelRepo.findByUnitId(unit.getId());
                                List<LevelProgressDTO> levelProgress = levels.stream()
                                        .map(level -> LevelProgressDTO.builder()
                                                .id(level.getId())
                                                .levelNumber(level.getLevelNumber())
                                                .levelName(level.getLevelName())
                                                .completed(levelCompletionMap.getOrDefault(level.getId(), false))
                                                .build())
                                        .collect(Collectors.toList());
                                
                                return UnitProgressDTO.builder()
                                        .id(unit.getId())
                                        .unitName(unit.getUnitName())
                                        .guidance(unit.getGuidance())
                                        .levels(levelProgress)
                                        .build();
                            })
                            .collect(Collectors.toList());
                    
                    return SectionProgressDTO.builder()
                            .id(section.getId())
                            .sectionName(section.getSectionName())
                            .units(unitProgress)
                            .build();
                })
                .collect(Collectors.toList());
        
        return CourseProgressDTO.builder()
                .id(course.getId())
                .courseName(course.getCourseName())
                .emoji(course.getEmoji())
                .sections(sectionProgress)
                .build();
    }
    
    public void enrollCourse(UUID courseId, String token) {
        String userId = extractUserId(token);
        
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        
        if (userCourseRepo.existsByUserIdAndCourseId(userId, courseId)) {
            throw new RuntimeException("User already enrolled in course: " + course.getCourseName());
        }
        
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        try {
            UserCourse userCourse = UserCourse.builder()
                    .user(user)
                    .course(course)
                    .courseXP(0)
                    .build();
            
            userCourseRepo.save(userCourse);
            System.out.println("User " + userId + " enrolled in course " + courseId);
        } catch (Exception e) {
            System.out.println("Error enrolling user " + userId + " in course " + courseId + ": " + e.getMessage());
            throw new RuntimeException("Failed to enroll in course");
        }
    }
    
    private String extractUserId(String token) {
        if (token == null || token.isBlank() || !token.startsWith("Bearer ")) {
            throw new ResourceNotFoundException("Token not found or invalid format");
        }
        try {
            return firebaseUtil.extractUidFromToken(token.substring(7));
        } catch (Exception e) {
            System.out.println("Error extracting user ID from token: " + e.getMessage());
            throw new ResourceNotFoundException("Invalid or expired token");
        }
    }
    
    private Map<UUID, Boolean> getLevelCompletionMap(String userId, UUID courseId) {
        try {
            List<UserLevelProgress> progressList = progressRepo.findByUserIdAndCourseId(userId, courseId);
            return progressList.stream()
                    .filter(UserLevelProgress::isCompleted)
                    .collect(Collectors.toMap(
                            progress -> progress.getLevel().getId(),
                            UserLevelProgress::isCompleted,
                            (existing, replacement) -> existing
                    ));
        } catch (Exception e) {
            System.out.println("Error fetching level completion map for user " + userId + " and course " + courseId + ": " + e.getMessage());
            throw new RuntimeException("Failed to fetch progress data");
        }
    }
    
    public CourseDTO createCourse(CourseDTO courseDTO) {
        try {
            Course course = courseMapper.toEntity(courseDTO);
            Course savedCourse = courseRepo.save(course);
            System.out.println("Course created with id: " + savedCourse.getId());
            return courseMapper.toDTO(savedCourse);
        } catch (Exception e) {
            System.out.println("Error creating course: " + e.getMessage());
            throw new RuntimeException("Failed to create course");
        }
    }
    
    public CourseDTO updateCourse(UUID courseId, CourseDTO courseDTO) {
        Course existingCourse = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        
        try {
            existingCourse.setCourseName(courseDTO.getCourseName());
            existingCourse.setEmoji(courseDTO.getEmoji());
            Course updatedCourse = courseRepo.save(existingCourse);
            System.out.println("Course updated with id: " + courseId);
            return courseMapper.toDTO(updatedCourse);
        } catch (Exception e) {
            System.out.println("Error updating course " + courseId + ": " + e.getMessage());
            throw new RuntimeException("Failed to update course");
        }
    }
    
    public void deleteCourse(UUID courseId) {
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        
        try {
            courseRepo.delete(course);
            System.out.println("Course deleted with id: " + courseId);
        } catch (Exception e) {
            System.out.println("Error deleting course " + courseId + ": " + e.getMessage());
            throw new RuntimeException("Failed to delete course");
        }
    }
}