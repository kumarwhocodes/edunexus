package dev.kumar.edunexus.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SectionDTO {
    private UUID id;
    private String sectionName;
    private UUID courseId;
}