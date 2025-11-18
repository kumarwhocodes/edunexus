package dev.kumar.edunexus.service;

import dev.kumar.edunexus.dto.PredictiveAnalyticsDTO;
import dev.kumar.edunexus.entity.*;
import dev.kumar.edunexus.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PerformanceAnalyticsAI {
    
    private final UserLevelProgressRepository progressRepo;
    private final UserRepository userRepo;
    private final CourseRepository courseRepo;
    private final UserCourseRepository userCourseRepo;
    
    public PredictiveAnalyticsDTO predictPerformance(String userId, UUID courseId) {
        List<UserLevelProgress> courseProgress = progressRepo.findByUserIdAndCourseId(userId, courseId);
        
        if (courseProgress.isEmpty()) {
            return createDefaultPrediction(userId, courseId);
        }
        
        int completedLevels = courseProgress.size();
        double avgDaysPerLevel = calculateAverageDaysPerLevel(courseProgress);
        int remainingLevels = estimateRemainingLevels(courseId, completedLevels);
        
        int estimatedDays = (int) (remainingLevels * avgDaysPerLevel);
        double successProb = calculateSuccessProbability(courseProgress);
        int recommendedDaily = calculateRecommendedDailyLevels(courseProgress);
        String trend = analyzeDifficultyTrend(courseProgress);
        double burnoutRisk = calculateBurnoutRisk(userId);
        
        return PredictiveAnalyticsDTO.builder()
                .userId(userId)
                .courseId(courseId.toString())
                .estimatedDaysToComplete(estimatedDays)
                .successProbability(successProb)
                .recommendedDailyLevels(recommendedDaily)
                .difficultyTrend(trend)
                .burnoutRisk(burnoutRisk)
                .build();
    }
    
    private LocalTime calculateOptimalStudyTime(List<UserLevelProgress> progress) {
        Map<Integer, Long> hourCounts = progress.stream()
                .filter(p -> p.getCompletedAt() != null)
                .collect(Collectors.groupingBy(
                        p -> p.getCompletedAt().getHour(),
                        Collectors.counting()
                ));
        
        return hourCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(entry -> LocalTime.of(entry.getKey(), 0))
                .orElse(LocalTime.of(18, 0));
    }
    
    private Map<String, Double> calculateCourseProgress(String userId) {
        List<UserCourse> userCourses = userCourseRepo.findByUserId(userId);
        Map<String, Double> progress = new HashMap<>();
        
        for (UserCourse uc : userCourses) {
            String courseName = uc.getCourse().getCourseName();
            int completed = progressRepo.findByUserIdAndCourseId(userId, uc.getCourse().getId()).size();
            int total = Math.max(1, estimateRemainingLevels(uc.getCourse().getId(), 0));
            progress.put(courseName, (completed * 100.0) / total);
        }
        
        return progress;
    }
    
    private String determineLearningPattern(List<UserLevelProgress> progress) {
        if (progress.size() < 3) return "new_learner";
        
        long recentActivity = progress.stream()
                .filter(p -> p.getCompletedAt() != null)
                .filter(p -> p.getCompletedAt().isAfter(LocalDate.now().minusDays(7).atStartOfDay()))
                .count();
        
        if (recentActivity >= 5) return "intensive";
        if (recentActivity >= 2) return "consistent";
        return "sporadic";
    }
    
    private double calculateWeeklyActivity(List<UserLevelProgress> progress) {
        long weeklyCount = progress.stream()
                .filter(p -> p.getCompletedAt() != null)
                .filter(p -> p.getCompletedAt().isAfter(LocalDate.now().minusDays(7).atStartOfDay()))
                .count();
        return weeklyCount;
    }
    
    private List<String> identifyStrongSubjects(Map<String, Double> courseProgress) {
        return courseProgress.entrySet().stream()
                .filter(entry -> entry.getValue() > 70.0)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
    
    private List<String> identifyWeakSubjects(Map<String, Double> courseProgress) {
        return courseProgress.entrySet().stream()
                .filter(entry -> entry.getValue() < 30.0)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
    
    private double calculateAverageDaysPerLevel(List<UserLevelProgress> progress) {
        if (progress.size() < 2) return 1.0;
        
        List<UserLevelProgress> sorted = progress.stream()
                .filter(p -> p.getCompletedAt() != null)
                .sorted((a, b) -> a.getCompletedAt().compareTo(b.getCompletedAt()))
                .collect(Collectors.toList());
        
        if (sorted.size() < 2) return 1.0;
        
        long totalDays = sorted.get(sorted.size() - 1).getCompletedAt().toLocalDate()
                .toEpochDay() - sorted.get(0).getCompletedAt().toLocalDate().toEpochDay();
        
        return Math.max(0.5, (double) totalDays / sorted.size());
    }
    
    private int estimateRemainingLevels(UUID courseId, int completed) {
        // Simple estimation - in real scenario, you'd count actual levels
        return Math.max(1, 20 - completed);
    }
    
    private double calculateSuccessProbability(List<UserLevelProgress> progress) {
        if (progress.isEmpty()) return 0.5;
        
        double avgXp = progress.stream().mapToInt(UserLevelProgress::getXpEarned).average().orElse(0);
        double consistency = calculateConsistency(progress);
        
        return Math.min(0.95, 0.3 + (avgXp / 100.0) * 0.4 + consistency * 0.3);
    }
    
    private int calculateRecommendedDailyLevels(List<UserLevelProgress> progress) {
        double avgPerformance = progress.stream()
                .mapToInt(UserLevelProgress::getXpEarned)
                .average().orElse(50);
        
        if (avgPerformance > 80) return 3;
        if (avgPerformance > 60) return 2;
        return 1;
    }
    
    private String analyzeDifficultyTrend(List<UserLevelProgress> progress) {
        if (progress.size() < 3) return "stable";
        
        List<Integer> recentXp = progress.stream()
                .sorted((a, b) -> b.getCompletedAt().compareTo(a.getCompletedAt()))
                .limit(5)
                .mapToInt(UserLevelProgress::getXpEarned)
                .boxed()
                .collect(Collectors.toList());
        
        double avgRecent = recentXp.stream().mapToInt(Integer::intValue).average().orElse(0);
        double avgOverall = progress.stream().mapToInt(UserLevelProgress::getXpEarned).average().orElse(0);
        
        if (avgRecent > avgOverall * 1.1) return "increasing";
        if (avgRecent < avgOverall * 0.9) return "decreasing";
        return "stable";
    }
    
    private double calculateBurnoutRisk(String userId) {
        List<UserLevelProgress> recentProgress = progressRepo.findByUserIdAndCompletedAtDate(userId, LocalDate.now());
        List<UserLevelProgress> yesterdayProgress = progressRepo.findByUserIdAndCompletedAtDate(userId, LocalDate.now().minusDays(1));
        
        int todayCount = recentProgress.size();
        int yesterdayCount = yesterdayProgress.size();
        
        if (todayCount > 10 || yesterdayCount > 10) return 0.8;
        if (todayCount > 5 || yesterdayCount > 5) return 0.4;
        return 0.1;
    }
    
    private double calculateConsistency(List<UserLevelProgress> progress) {
        if (progress.size() < 7) return 0.5;
        
        Set<LocalDate> uniqueDays = progress.stream()
                .filter(p -> p.getCompletedAt() != null)
                .map(p -> p.getCompletedAt().toLocalDate())
                .collect(Collectors.toSet());
        
        return Math.min(1.0, uniqueDays.size() / 7.0);
    }
    
    private PredictiveAnalyticsDTO createDefaultPrediction(String userId, UUID courseId) {
        return PredictiveAnalyticsDTO.builder()
                .userId(userId)
                .courseId(courseId.toString())
                .estimatedDaysToComplete(30)
                .successProbability(0.7)
                .recommendedDailyLevels(1)
                .difficultyTrend("stable")
                .burnoutRisk(0.1)
                .build();
    }
    
    private List<UserLevelProgress> getAllUserProgress(String userId) {
        return progressRepo.findAll().stream()
                .filter(p -> p.getUser().getId().equals(userId))
                .collect(Collectors.toList());
    }
}