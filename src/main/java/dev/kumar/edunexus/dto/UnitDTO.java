package dev.kumar.edunexus.dto;

import lombok.*;

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
    private String guidance;
    private UUID sectionId;
}