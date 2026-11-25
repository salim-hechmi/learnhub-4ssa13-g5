package com.learnhub.course.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/** DTO de requête pour créer ou mettre à jour un chapitre. */
@Data
public class ChapterRequestDTO {

    @NotBlank(message = "Le titre du chapitre est obligatoire")
    private String title;

    @Positive(message = "La position doit être un entier positif")
    private Integer position;

    @NotNull(message = "L'ID du cours parent est obligatoire")
    private Integer courseId;
}
