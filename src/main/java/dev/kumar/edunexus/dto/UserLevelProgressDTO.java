package dev.kumar.edunexus.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserLevelProgressDTO {
    private UUID id;
    private String userId;
    private UUID courseId;
    private UUID sectionId;
    private UUID levelId;
    private boolean completed;
    private int xpEarned;
    private LocalDateTime completedAt;
}