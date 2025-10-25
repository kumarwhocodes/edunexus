package dev.kumar.edunexus.service;

import com.google.firebase.auth.UserRecord;
import dev.kumar.edunexus.dto.*;
import dev.kumar.edunexus.dto.progress.SectionProgressDTO;
import dev.kumar.edunexus.entity.*;
import dev.kumar.edunexus.exception.ResourceNotFoundException;
import dev.kumar.edunexus.mapper.UserMapper;
import dev.kumar.edunexus.repository.*;
import dev.kumar.edunexus.util.FirebaseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository repo;
    private final UserMapper userMapper;
    private final FirebaseUtil firebaseUtil;
    private final UserCourseRepository userCourseRepo;
    private final SectionRepository sectionRepo;
    private final UnitRepository unitRepo;
    private final LevelRepository levelRepo;
    private final UserLevelProgressRepository progressRepo;
    
    // Login user with Firebase token
    public UserDTO loginUser(AccessTokenBody tokenBody) {
        String authHeader = tokenBody.getToken();
        if (authHeader == null || authHeader.isBlank() || !authHeader.startsWith("Bearer")) {
            throw new ResourceNotFoundException("Token not found or invalid format");
        }
        
        String token = authHeader.substring(7);
        UserRecord firebaseUser = firebaseUtil.fetchUserFromToken(token).orElseThrow(() -> new ResourceNotFoundException("Invalid token"));
        
        if (isUserPresent(firebaseUser))
            return userMapper.toDTO(fetchUserById(firebaseUser));
        else
            return createUser(firebaseUser);
    }
    
    // Fetch user by token
    public UserDTO fetchUser(String token) {
        if (token == null || token.isBlank() || !token.startsWith("Bearer")) {
            throw new ResourceNotFoundException("Token not found or invalid format");
        }
        
        String actualToken = token.substring(7);
        String uid = firebaseUtil.extractUidFromToken(actualToken);
        
        User user = repo.findById(uid).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        UserDTO userDTO = userMapper.toDTO(user);
        
        // Fetch enrolled courses with progress
        List<EnrolledCourseDTO> enrolledCourses = getEnrolledCoursesWithProgress(uid);
        userDTO.setEnrolledCourses(enrolledCourses);
        
        return userDTO;
    }
    
    // Update user profile
    public UserDTO updateUser(String token, UserDTO userDTO) {
        if (token == null || token.isBlank() || !token.startsWith("Bearer")) {
            throw new ResourceNotFoundException("Token not found or invalid format");
        }
        String actualToken = token.substring(7);
        String uid = firebaseUtil.extractUidFromToken(actualToken);
        
        User existingUser = repo.findById(uid)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + uid));
        
        existingUser.setName(userDTO.getName());
        existingUser.setProfileUrl(userDTO.getProfileUrl());
        
        User updatedUser = repo.save(existingUser);
        return userMapper.toDTO(updatedUser);
    }
    
    // Delete user account
    public void deleteUser(String token) {
        if (token == null || token.isBlank() || !token.startsWith("Bearer")) {
            throw new ResourceNotFoundException("Token not found or invalid format");
        }
        String actualToken = token.substring(7);
        String uid = firebaseUtil.extractUidFromToken(actualToken);
        
        User existingUser = repo.findById(uid)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + uid));
        
        firebaseUtil.deleteUser(uid);
        repo.delete(existingUser);
    }
    
    
    private boolean isUserPresent(UserRecord firebaseUser) {
        return repo.existsById(firebaseUser.getUid());
    }
    
    private User fetchUserById(UserRecord firebaseUser) {
        return repo
                .findById(firebaseUser.getUid())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + firebaseUser.getUid()));
    }
    
    private UserDTO createUser(UserRecord firebaseUser) {
        User user = User
                .builder()
                .id(firebaseUser.getUid())
                .name(firebaseUser.getDisplayName())
                .joinDate(LocalDate.now())
                .email(firebaseUser.getEmail())
                .profileUrl(firebaseUser.getPhotoUrl())
                .build();
        
        repo.save(user);
        return userMapper.toDTO(user);
    }
    
    private List<EnrolledCourseDTO> getEnrolledCoursesWithProgress(String userId) {
        List<UserCourse> userCourses = userCourseRepo.findByUserId(userId);
        
        return userCourses.stream()
                .map(userCourse -> {
                    Course course = userCourse.getCourse();
                    List<SectionProgressDTO> progress = getCourseProgress(course.getId(), userId);
                    
                    return EnrolledCourseDTO.builder()
                            .id(course.getId())
                            .courseName(course.getCourseName())
                            .emoji(course.getEmoji())
                            .courseXP(userCourse.getCourseXP())
                            .progress(progress)
                            .build();
                })
                .collect(Collectors.toList());
    }
    
    private List<SectionProgressDTO> getCourseProgress(UUID courseId, String userId) {
        List<Section> sections = sectionRepo.findByCourseId(courseId);
        Map<UUID, Boolean> levelCompletionMap = getLevelCompletionMap(userId, courseId);
        
        return sections.stream()
                .map(section -> {
                    List<Unit> units = unitRepo.findBySectionId(section.getId());
                    return SectionProgressDTO.builder()
                            .id(section.getId())
                            .sectionName(section.getSectionName())
                            .units(units.stream()
                                    .map(unit -> {
                                        List<Level> levels = levelRepo.findByUnitId(unit.getId());
                                        return dev.kumar.edunexus.dto.progress.UnitProgressDTO.builder()
                                                .id(unit.getId())
                                                .number(unit.getNumber())
                                                .guidance(unit.getGuidance())
                                                .levels(levels.stream()
                                                        .map(level -> dev.kumar.edunexus.dto.progress.LevelProgressDTO.builder()
                                                                .id(level.getId())
                                                                .levelNumber(level.getLevelNumber())
                                                                .completed(levelCompletionMap.getOrDefault(level.getId(), false))
                                                                .build())
                                                        .collect(Collectors.toList()))
                                                .build();
                                    })
                                    .collect(Collectors.toList()))
                            .build();
                })
                .collect(Collectors.toList());
    }
    
    private Map<UUID, Boolean> getLevelCompletionMap(String userId, UUID courseId) {
        List<UserLevelProgress> progressList = progressRepo.findByUserIdAndCourseId(userId, courseId);
        return progressList.stream()
                .collect(Collectors.toMap(
                        progress -> progress.getLevel().getId(),
                        UserLevelProgress::isCompleted
                ));
    }
}
