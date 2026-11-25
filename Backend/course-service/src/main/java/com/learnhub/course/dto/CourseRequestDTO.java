package com.learnhub.course.dto;

import com.learnhub.course.model.CourseLevel;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

/** DTO de requête pour créer ou mettre à jour un cours. */
@Data
public class CourseRequestDTO {

    @NotBlank(message = "Le titre du cours est obligatoire")
    @Size(max = 200, message = "Le titre ne peut pas dépasser 200 caractères")
    private String title;

    private String description;

    @DecimalMin(value = "0.0", inclusive = true, message = "Le prix doit être positif ou nul")
    private BigDecimal price;

    private CourseLevel level;

    @NotBlank(message = "L'ID de l'instructeur est obligatoire")
    private String instructorId;

    private Integer categoryId;
}
