package com.learnhub.course.service;

import com.learnhub.course.dto.*;
import java.util.List;

public interface LessonService {
    List<LessonResponseDTO> getLessonsByChapter(Integer chapterId);
    LessonResponseDTO createLesson(LessonRequestDTO request);
    LessonResponseDTO updateLesson(Integer id, LessonRequestDTO request);
    void deleteLesson(Integer id);
}
