package dev.kumar.edunexus.service;

import dev.kumar.edunexus.dto.LevelDTO;
import dev.kumar.edunexus.entity.Level;
import dev.kumar.edunexus.entity.Unit;
import dev.kumar.edunexus.entity.User;
import dev.kumar.edunexus.entity.UserLevelProgress;
import dev.kumar.edunexus.exception.ResourceNotFoundException;
import dev.kumar.edunexus.repository.*;
import dev.kumar.edunexus.entity.UserCourse;
import dev.kumar.edunexus.util.FirebaseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LevelService {
    
    private final LevelRepository levelRepo;
    private final UserLevelProgressRepository progressRepo;
    private final UserRepository userRepo;
    private final FirebaseUtil firebaseUtil;
    private final UnitRepository unitRepo;
    private final UserCourseRepository userCourseRepo;
    
    @Transactional
    public void completeLevel(UUID levelId, String token, int xpEarned) {
        String userId = extractUserId(token);
        
        Level level = levelRepo.findById(levelId)
                .orElseThrow(() -> new ResourceNotFoundException("Level not found with id: " + levelId));
        
        UUID courseId = level.getUnit().getSection().getCourse().getId();
        if(!userCourseRepo.existsByUserIdAndCourseId(userId, courseId)){
            throw new ResourceNotFoundException("User not enrolled in course");
        }
        
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        try {
            UserLevelProgress progress = UserLevelProgress.builder()
                    .user(user)
                    .course(level.getUnit().getSection().getCourse())
                    .section(level.getUnit().getSection())
                    .level(level)
                    .completed(true)
                    .xpEarned(xpEarned)
                    .completedAt(LocalDateTime.now())
                    .build();
            
            progressRepo.save(progress);
            
            user.setTotalXP(user.getTotalXP() + xpEarned);
            updateStreak(user);
            userRepo.save(user);
            
            // Update course XP
            userCourseRepo.findByUserId(userId).stream()
                    .filter(uc -> uc.getCourse().getId().equals(courseId))
                    .findFirst()
                    .ifPresent(userCourse -> {
                        userCourse.setCourseXP(userCourse.getCourseXP() + xpEarned);
                        userCourseRepo.save(userCourse);
                    });
            
            System.out.println("User " + userId + " completed level " + levelId + " and earned " + xpEarned + " XP");
        } catch (Exception e) {
            System.out.println("Error completing level " + levelId + " for user " + userId + ": " + e.getMessage());
            throw new RuntimeException("Failed to complete level");
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
    
    public List<LevelDTO> getLevelsByUnit(UUID unitId) {
        if(!unitRepo.existsById(unitId)){
            throw new ResourceNotFoundException("Unit not found with id: " + unitId);
        }
        try {
            return levelRepo.findByUnitId(unitId).stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println("Error fetching levels for unit " + unitId + ": " + e.getMessage());
            throw new RuntimeException("Failed to fetch levels");
        }
    }
    
    public LevelDTO createLevel(LevelDTO levelDTO) {
        if (!unitRepo.existsById(levelDTO.getUnitId())) {
            throw new ResourceNotFoundException("Unit not found with id: " + levelDTO.getUnitId());
        }
        
        try {
            Level level = toEntity(levelDTO);
            Level savedLevel = levelRepo.save(level);
            System.out.println("Level created with id: " + savedLevel.getId());
            return toDTO(savedLevel);
        } catch (Exception e) {
            System.out.println("Error creating level: " + e.getMessage());
            throw new RuntimeException("Failed to create level");
        }
    }
    
    public LevelDTO updateLevel(UUID levelId, LevelDTO levelDTO) {
        Level existingLevel = levelRepo.findById(levelId)
                .orElseThrow(() -> new ResourceNotFoundException("Level not found with id: " + levelId));
        
        try {
            existingLevel.setLevelNumber(levelDTO.getLevelNumber());
            existingLevel.setLevelName(levelDTO.getLevelName());
            Level updatedLevel = levelRepo.save(existingLevel);
            System.out.println("Level updated with id: " + levelId);
            return toDTO(updatedLevel);
        } catch (Exception e) {
            System.out.println("Error updating level " + levelId + ": " + e.getMessage());
            throw new RuntimeException("Failed to update level");
        }
    }
    
    public void deleteLevel(UUID levelId) {
        Level level = levelRepo.findById(levelId)
                .orElseThrow(() -> new ResourceNotFoundException("Level not found with id: " + levelId));
        
        try {
            levelRepo.delete(level);
            System.out.println("Level deleted with id: " + levelId);
        } catch (Exception e) {
            System.out.println("Error deleting level " + levelId + ": " + e.getMessage());
            throw new RuntimeException("Failed to delete level");
        }
    }
    
    private LevelDTO toDTO(Level level) {
        return LevelDTO.builder()
                .id(level.getId())
                .levelNumber(level.getLevelNumber())
                .levelName(level.getLevelName())
                .unitId(level.getUnit().getId())
                .build();
    }
    
    private Level toEntity(LevelDTO dto) {
        return Level.builder()
                .levelNumber(dto.getLevelNumber())
                .levelName(dto.getLevelName())
                .unit(Unit.builder().id(dto.getUnitId()).build())
                .build();
    }
    
    private void updateStreak(User user) {
        LocalDate today = LocalDate.now();
        List<UserLevelProgress> todayProgress = progressRepo.findByUserIdAndCompletedAtDate(user.getId(), today);
        
        if (todayProgress.size() == 1) { // First completion today
            List<UserLevelProgress> yesterdayProgress = progressRepo.findByUserIdAndCompletedAtDate(user.getId(), today.minusDays(1));
            if (!yesterdayProgress.isEmpty()) {
                user.setDayStreak(user.getDayStreak() + 1);
            } else {
                user.setDayStreak(1);
            }
        }
    }
}