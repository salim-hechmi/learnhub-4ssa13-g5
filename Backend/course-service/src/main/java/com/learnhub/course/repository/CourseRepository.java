package com.learnhub.course.repository;

import com.learnhub.course.model.Course;
import com.learnhub.course.model.CourseLevel;
import com.learnhub.course.model.CourseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {

    Page<Course> findByStatus(CourseStatus status, Pageable pageable);

    Page<Course> findByStatusAndCategory_Id(CourseStatus status, Integer categoryId, Pageable pageable);

    Page<Course> findByStatusAndLevel(CourseStatus status, CourseLevel level, Pageable pageable);

    List<Course> findByInstructorId(String instructorId);

    @Query("SELECT c FROM Course c WHERE c.status = :status AND " +
           "(LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Course> searchPublished(@Param("status") CourseStatus status,
                                 @Param("keyword") String keyword,
                                 Pageable pageable);
}
