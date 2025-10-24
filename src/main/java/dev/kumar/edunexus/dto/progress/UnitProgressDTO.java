package dev.kumar.edunexus.dto.progress;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UnitProgressDTO {
    private UUID id;
    private int number;
    private String guidance;
    private List<LevelProgressDTO> levels;
}