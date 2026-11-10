package com.learnhub.course.model;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * Entité métier Course propre au contexte borné de course-service.
 * Règle AWD : Une base par service, aucune relation JPA inter-services.
 * Les références externes sont stockées sous forme d'identifiants simples (Long).
 */
@Entity
@Table(name = "courses")
public class Course implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String subtitle;
    private String description;
    private Long instructorId;
    private Long categoryId;
    private Double price;
    private Boolean published = false;
    private Double averageRating = 0.0;
    private Integer reviewsCount = 0;
    private java.time.LocalDateTime createdAt = java.time.LocalDateTime.now();

    public Course() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
