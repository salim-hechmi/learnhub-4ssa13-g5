package com.learnhub.course.dto;

import lombok.Builder;
import lombok.Data;

/** DTO de réponse pour une catégorie. */
@Data
@Builder
public class CategoryResponseDTO {
    private Integer id;
    private String name;
    private String description;
    private int coursesCount;
}
