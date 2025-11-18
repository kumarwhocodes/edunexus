package dev.kumar.edunexus.dto;

import lombok.*;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UnitDTO {
    private UUID id;
    private String unitName;
    private Map<String, Object> guidance;
    private UUID sectionId;
}