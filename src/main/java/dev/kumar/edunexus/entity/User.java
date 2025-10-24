package dev.kumar.edunexus.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    private String id;
    
    private String name;
    private String username;
    
    @Column(unique = true)
    private String email;
    
    private LocalDate joinDate;
    private int dayStreak;
    private int totalXP;
    private int hearts;
    private String profileUrl;
}