package com.learnhub.course.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/** DTO de réponse pour un chapitre avec ses leçons. */
@Data
@Builder
public class ChapterResponseDTO {
    private Integer id;
    private String title;
    private Integer position;
    private Integer courseId;
    private List<LessonResponseDTO> lessons;
}
