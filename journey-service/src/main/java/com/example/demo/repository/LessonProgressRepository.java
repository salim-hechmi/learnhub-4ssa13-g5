package com.example.demo.repository;

import com.example.demo.entity.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {
    boolean existsByEnrollmentIdAndLessonId(Long enrollmentId, Long lessonId);
    long countByEnrollmentIdAndCompletedTrue(Long enrollmentId);
}
