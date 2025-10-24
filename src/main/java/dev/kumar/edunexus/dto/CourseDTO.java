package dev.kumar.edunexus.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {
    private UUID id;
    private String courseName;
    private String emoji;
}