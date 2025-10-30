package dev.kumar.edunexus.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDTO {
    private UUID id;
    private int type; // 1=single correct, 2=multi correct, 3=match correct
    private String question;
    private List<String> options;
    private List<String> correctAnswers;
    private UUID levelId;
}