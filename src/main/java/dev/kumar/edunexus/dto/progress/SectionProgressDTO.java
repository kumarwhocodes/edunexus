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
public class SectionProgressDTO {
    private UUID id;
    private String sectionName;
    private List<UnitProgressDTO> units;
}