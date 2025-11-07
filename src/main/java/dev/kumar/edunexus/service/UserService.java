package dev.kumar.edunexus.service;

import com.google.firebase.auth.UserRecord;
import dev.kumar.edunexus.dto.*;
import dev.kumar.edunexus.entity.*;
import dev.kumar.edunexus.exception.ConflictException;
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
            return createUser(firebaseUser, tokenBody.getUsername());
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
    
    private UserDTO createUser(UserRecord firebaseUser, String username) {
        if (repo.existsByUsername(username.trim())) {
            throw new ConflictException("Username already exists");
        }
        User user = User
                .builder()
                .id(firebaseUser.getUid())
                .name(firebaseUser.getDisplayName())
                .username(username.trim())
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
                    int completedLevels = getCompletedLevelsCount(course.getId(), userId);
                    int totalLevels = getTotalLevelsCount(course.getId());
                    
                    return EnrolledCourseDTO.builder()
                            .id(course.getId())
                            .courseName(course.getCourseName())
                            .emoji(course.getEmoji())
                            .courseXP(userCourse.getCourseXP())
                            .completedLevels(completedLevels)
                            .totalLevels(totalLevels)
                            .build();
                })
                .collect(Collectors.toList());
    }
    
    private int getCompletedLevelsCount(UUID courseId, String userId) {
        List<UserLevelProgress> progressList = progressRepo.findByUserIdAndCourseId(userId, courseId);
        return (int) progressList.stream()
                .filter(UserLevelProgress::isCompleted)
                .map(progress -> progress.getLevel().getId())
                .distinct()
                .count();
    }
    
    private int getTotalLevelsCount(UUID courseId) {
        List<Section> sections = sectionRepo.findByCourseId(courseId);
        return sections.stream()
                .mapToInt(section -> {
                    List<Unit> units = unitRepo.findBySectionId(section.getId());
                    return units.stream()
                            .mapToInt(unit -> levelRepo.findByUnitId(unit.getId()).size())
                            .sum();
                })
                .sum();
    }
}
