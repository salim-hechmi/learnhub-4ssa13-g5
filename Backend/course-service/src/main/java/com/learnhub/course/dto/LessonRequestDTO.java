package com.learnhub.course.dto;

import com.learnhub.course.model.LessonType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/** DTO de requête pour créer ou mettre à jour une leçon. */
@Data
public class LessonRequestDTO {

    @NotBlank(message = "Le titre de la leçon est obligatoire")
    private String title;

    private LessonType type;

    private String contentUrl;

    private Integer durationMin;

    private Integer position;

    private Boolean isFreePreview;

    @NotNull(message = "L'ID du chapitre parent est obligatoire")
    private Integer chapterId;
}
