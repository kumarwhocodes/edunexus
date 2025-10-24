package dev.kumar.edunexus.repository;

import dev.kumar.edunexus.entity.UserLevelProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserLevelProgressRepository extends JpaRepository<UserLevelProgress, UUID> {
    List<UserLevelProgress> findByUserIdAndCourseId(String userId, UUID courseId);
    boolean existsByUserIdAndLevelId(String userId, UUID levelId);
}