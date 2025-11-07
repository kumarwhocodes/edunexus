package dev.kumar.edunexus.dto;

import lombok.*;


@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AccessTokenBody {
    private String token;
    private String username;
}