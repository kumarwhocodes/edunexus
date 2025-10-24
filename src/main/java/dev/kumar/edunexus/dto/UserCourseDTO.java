package dev.kumar.edunexus.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserCourseDTO {
    private UUID id;
    private String userId;
    private UUID courseId;
    private int courseXP;
}