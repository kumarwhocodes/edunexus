package dev.kumar.edunexus.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDTO {
    private UUID id;
    private int type;
    private String question;
    private UUID levelId;
}