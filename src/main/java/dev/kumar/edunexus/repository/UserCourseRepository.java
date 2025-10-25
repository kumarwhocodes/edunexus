package dev.kumar.edunexus.repository;

import dev.kumar.edunexus.entity.UserCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserCourseRepository extends JpaRepository<UserCourse, UUID> {
    boolean existsByUserIdAndCourseId(String userId, UUID courseId);
    List<UserCourse> findByUserId(String userId);
}