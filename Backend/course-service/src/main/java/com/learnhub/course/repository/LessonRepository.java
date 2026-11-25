package com.learnhub.course.repository;

import com.learnhub.course.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Integer> {
    List<Lesson> findByChapter_IdOrderByPositionAsc(Integer chapterId);
}
