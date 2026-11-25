package com.learnhub.course.dto;

import com.learnhub.course.model.CourseLevel;
import com.learnhub.course.model.CourseStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** DTO de réponse complet pour un cours. */
@Data
@Builder
public class CourseResponseDTO {
    private Integer id;
    private String title;
    private String description;
    private BigDecimal price;
    private CourseLevel level;
    private CourseStatus status;
    private String instructorId;
    private Integer categoryId;
    private String categoryName;
    private LocalDateTime createdAt;
    private int chaptersCount;
}
