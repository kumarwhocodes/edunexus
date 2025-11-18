package dev.kumar.edunexus.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EnrolledCourseDTO {
    private UUID id;
    private String courseName;
    private String emoji;
    private int courseXP;
    private int completedLevels;
    private int totalLevels;
    private int estimatedDaysToComplete;
    private double successProbability;
    private int recommendedDailyLevels;
    private String difficultyTrend;
    private double burnoutRisk;
}