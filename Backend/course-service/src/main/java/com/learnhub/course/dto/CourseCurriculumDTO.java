package com.learnhub.course.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * DTO de réponse pour le curriculum complet d'un cours.
 * Endpoint : GET /api/courses/{id}/curriculum
 */
@Data
@Builder
public class CourseCurriculumDTO {
    private Integer courseId;
    private String courseTitle;
    private List<ChapterResponseDTO> chapters;
    private int totalLessons;
    private int totalDurationMin;
}
