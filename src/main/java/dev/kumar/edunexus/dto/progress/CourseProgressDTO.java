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
public class CourseProgressDTO {
    private UUID id;
    private String courseName;
    private String emoji;
    private List<SectionProgressDTO> sections;
}