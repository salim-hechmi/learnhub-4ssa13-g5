package com.learnhub.course.model;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * Entité métier LearningResource propre au contexte borné de course-service.
 * Règle AWD : Une base par service, aucune relation JPA inter-services.
 * Les références externes sont stockées sous forme d'identifiants simples (Long).
 */
@Entity
@Table(name = "learningresources")
public class LearningResource implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long chapterId;
    private String title;
    private String resourceType;
    private String resourceUrl;
    private Integer durationMinutes;

    public LearningResource() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
