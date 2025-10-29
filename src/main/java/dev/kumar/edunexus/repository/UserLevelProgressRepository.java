package dev.kumar.edunexus.repository;

import dev.kumar.edunexus.entity.UserLevelProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface UserLevelProgressRepository extends JpaRepository<UserLevelProgress, UUID> {
    List<UserLevelProgress> findByUserIdAndCourseId(String userId, UUID courseId);
    boolean existsByUserIdAndLevelId(String userId, UUID levelId);
    
    @Query("SELECT p FROM UserLevelProgress p WHERE p.user.id = :userId AND DATE(p.completedAt) = :date")
    List<UserLevelProgress> findByUserIdAndCompletedAtDate(@Param("userId") String userId, @Param("date") LocalDate date);
}