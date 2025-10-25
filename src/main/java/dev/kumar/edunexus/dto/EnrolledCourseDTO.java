package dev.kumar.edunexus.dto;

import dev.kumar.edunexus.dto.progress.SectionProgressDTO;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EnrolledCourseDTO {
    private UUID id;
    private String courseName;
    private String emoji;
    private int courseXP;
    private List<SectionProgressDTO> progress;
}