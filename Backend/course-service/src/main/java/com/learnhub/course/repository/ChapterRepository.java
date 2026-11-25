package com.learnhub.course.repository;

import com.learnhub.course.model.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Integer> {
    List<Chapter> findByCourse_IdOrderByPositionAsc(Integer courseId);
}
