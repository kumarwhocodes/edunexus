package dev.kumar.edunexus.dto.progress;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LevelProgressDTO {
    private UUID id;
    private int levelNumber;
    private String levelName;
    private boolean completed; // for tick/cross display
}