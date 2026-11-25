package com.learnhub.course.dto;

import com.learnhub.course.model.LessonType;
import lombok.Builder;
import lombok.Data;

/** DTO de réponse pour une leçon. */
@Data
@Builder
public class LessonResponseDTO {
    private Integer id;
    private String title;
    private LessonType type;
    private String contentUrl;
    private Integer durationMin;
    private Integer position;
    private Boolean isFreePreview;
    private Integer chapterId;
}
