package dev.kumar.edunexus.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OptionDTO {
    private UUID id;
    private String text;
    private boolean isAnswer;
    private UUID questionId;
}