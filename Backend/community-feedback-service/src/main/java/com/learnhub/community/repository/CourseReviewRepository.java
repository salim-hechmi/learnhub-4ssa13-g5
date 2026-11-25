package com.learnhub.community.repository;

import com.learnhub.community.model.CourseReview;
import com.learnhub.community.model.ModerationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CourseReviewRepository extends JpaRepository<CourseReview, String> {
    List<CourseReview> findByModerationStatus(ModerationStatus status);
    List<CourseReview> findByCourseId(String courseId);
}
