package dev.kumar.edunexus.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String id;
    private String name;
    private String username;
    private String email;
    private LocalDate joinDate;
    private int dayStreak;
    private int totalXP;
    private int hearts;
    private String profileUrl;
    private List<EnrolledCourseDTO> enrolledCourses;
}
