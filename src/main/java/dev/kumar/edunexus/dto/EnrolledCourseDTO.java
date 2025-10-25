package dev.kumar.edunexus.dto;

import dev.kumar.edunexus.dto.progress.CourseProgressDTO;
import lombok.*;

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
    private CourseProgressDTO progress;
}