package dev.kumar.edunexus.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LevelDTO {
    private UUID id;
    private int levelNumber;
    private String levelName;
    private UUID unitId;
    private boolean completed; // for progress tracking
}