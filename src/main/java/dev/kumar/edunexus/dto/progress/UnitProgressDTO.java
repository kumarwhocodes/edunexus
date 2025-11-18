package dev.kumar.edunexus.dto.progress;

import lombok.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UnitProgressDTO {
    private UUID id;
    private String unitName;
    private Map<String, Object> guidance;
    private List<LevelProgressDTO> levels;
}