package com.learnhub.course.service;

import com.learnhub.course.dto.*;
import java.util.List;

public interface ChapterService {
    List<ChapterResponseDTO> getChaptersByCourse(Integer courseId);
    ChapterResponseDTO createChapter(ChapterRequestDTO request);
    ChapterResponseDTO updateChapter(Integer id, ChapterRequestDTO request);
    void deleteChapter(Integer id);
}
