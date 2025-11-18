package dev.kumar.edunexus.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PredictiveAnalyticsDTO {
    private String userId;
    private String courseId;
    private int estimatedDaysToComplete;
    private double successProbability;
    private int recommendedDailyLevels;
    private String difficultyTrend; // "increasing", "stable", "decreasing"
    private double burnoutRisk; // 0.0 to 1.0
}